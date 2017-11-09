package com.jeroensteenbeeke.topiroll.beholder.web.components.mapcontrol;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableAdapter;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableBehavior;
import com.jeroensteenbeeke.hyperion.ducktape.web.components.TypedPanel;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MarkerService;
import com.jeroensteenbeeke.topiroll.beholder.entities.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.AreaMarkerFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.visitor.AreaMarkerVisitor;
import com.jeroensteenbeeke.topiroll.beholder.web.components.AbstractMapPreview;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.danekja.java.util.function.serializable.SerializableBiFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.Serializable;

public abstract class MoveMarkerController extends TypedPanel<MapView> {

	private static final String MARKER_ID = "marker";

	private static final Logger log = LoggerFactory.getLogger(MoveMarkerController.class);

	@Inject
	private MarkerService markerService;


	public MoveMarkerController(String id, MapView view) {
		super(id, ModelMaker.wrap(view));

		ScaledMap map = view.getSelectedMap();

		AreaMarkerFilter filter = new AreaMarkerFilter();
		filter.view().set(view);

		AbstractMapPreview previewImage = new AbstractMapPreview("map", map) {
			@Override
			protected void addOnDomReadyJavaScript(String canvasId, StringBuilder js,
												   double factor) {

			}
		};

		ListView<AreaMarker> mapMarkerView =
				new ListView<AreaMarker>("mapmarkers", ModelMaker.wrapList(view.getMarkers())) {
					@Override
					protected void populateItem(ListItem<AreaMarker> item) {
						AreaMarker areaMarker = item.getModelObject();

						int squareSize = map.getSquareSize();

						int wh = squareSize * areaMarker.getExtent() / 5;

						log.info("Marker of type {}", areaMarker.getClass());
						log.info("  Offset X = {}", areaMarker.getOffsetX());
						log.info("  Offset Y = {}", areaMarker.getOffsetY());
						MarkerStyleModel<?> markerStyleModel = areaMarker
								.visit(new AreaMarkerVisitor<MarkerStyleModel<?>>() {
									@Override
									public MarkerStyleModel<?> visit(
											@Nonnull
													CircleMarker marker) {
										return new MarkerStyleModel<>(marker, previewImage.getFactor())
												.setX((m, factor) -> Math.round(factor * m.getOffsetX()))
												.setY((m, factor) -> Math.round(factor * m.getOffsetY()))
												.setBackgroundColor((m, factor) -> marker.getColor())
												.setWidth((m, factor) -> Math.round(wh * factor * 2))
												.setHeight((m, factor) -> Math.round(wh * factor * 2))
												.setOpacity((m, factor) -> 0.5)
												.setBorderRadiusPercent((m, factor) -> 100L)
												.setTransform((m, factor) -> String.format
														("translate(%1$dpx,%1$dpx)",-wh));
									}

									@Override
									public MarkerStyleModel<?> visit(
											@Nonnull
													ConeMarker marker) {
										// CSS offset is interpreted as the top-left of the
										// element,
										// whereas the cone origin is halfway along the left border
										// This requires a translation based on the rotation of
										// the element
										double tx = Math.cos(Math.toRadians(marker.getTheta())) - 1;
										double ty = Math.sin(Math.toRadians(marker.getTheta()))
												- 2;


										return new MarkerStyleModel<>(marker, previewImage.getFactor()).setX((m, factor) -> Math.round((m.getOffsetX()) * factor)).setY((m, factor) -> Math.round(m.getOffsetY() * factor)).setWidth((m, factor) -> 0L)
												.setHeight((m, factor) -> 0L).setBorderTop((m, factor) -> String
														.format("%fpx solid transparent", wh * factor)).setBorderRight((m, factor) -> String.format("%fpx solid #%s",
														wh * factor, marker.getColor()))
												.setBorderBottom((m, factor) -> String.format("%fpx solid transparent", wh * factor))
												.setOpacity((m, factor) -> 0.5)
												.setBorderRadiusPercent((m, factor) -> 50L)
												.setTransform((m, factor) -> String
														.format("translate(%fpx, %fpx) rotate(%ddeg)",
																factor * tx * wh / 2, factor * ty *
																		wh / 2, m.getTheta()))

												;
									}

									@Override
									public MarkerStyleModel<?> visit(
											@Nonnull
													CubeMarker marker) {
										return new MarkerStyleModel<>(marker, previewImage.getFactor())
												.setX((m, factor) -> Math.round(factor * m.getOffsetX()))
												.setY((m, factor) -> Math.round(factor * m.getOffsetY()))
												.setWidth((m, factor) -> Math.round(wh * factor))
												.setHeight((m, factor) -> Math.round(wh * factor))
												.setBackgroundColor((m, factor) -> marker.getColor())
												.setOpacity((m, factor) -> 0.5)
												.setTransform((m, factor) -> String.format
														("translate(%1$dpx,%1$dpx)", -wh/2));
									}
									@Override
									public MarkerStyleModel<?> visit(
											@Nonnull
													LineMarker marker) {
										// CSS offset is interpreted as the top-left of the
										// element,
										// whereas the cone origin is halfway along the left border
										// This requires a translation based on the rotation of
										// the element
										double tx = Math.cos(Math.toRadians(marker.getTheta())) - 1;
										double ty = Math.sin(Math.toRadians(marker.getTheta()));


										return new MarkerStyleModel<>(marker, previewImage.getFactor()).setX((m, factor) -> Math.round((m.getOffsetX()) * factor)).setY((m, factor) -> Math.round(m.getOffsetY() * factor)).setWidth((m, factor) -> 0L)
												.setHeight((m, factor) -> 0L).setBorderTop((m, factor) -> "1px solid transparent").setBorderRight((m, factor) -> String.format("%fpx solid #%s",
														wh * factor, marker.getColor()))
												.setBorderBottom((m, factor) -> "1px solid transparent")
												.setOpacity((m, factor) -> 0.5)
												.setBorderRadiusPercent((m, factor) -> 50L)
												.setTransform((m, factor) -> String
														.format("translate(%fpx, %fpx) rotate(%ddeg)",
																factor * tx * wh / 2, factor * ty *
																		(wh / 2 - 2), m.getTheta()))

												;
									}
								});

						log.info("CSS: {}", markerStyleModel.getObject());

						log.info("===========");


						WebMarkupContainer marker = new WebMarkupContainer(MARKER_ID);
						marker.setOutputMarkupId(true);
						marker.add(AttributeModifier.replace("style", markerStyleModel));


						Options draggableOptions = new Options();
						draggableOptions.set("opacity", "0.5");
						draggableOptions.set("containment", Options.asString("parent"));

						marker.add(new DraggableBehavior(draggableOptions,
								new DraggableAdapter() {
									private static final long serialVersionUID = 1L;

									@Override
									public boolean isStopEventEnabled() {

										return true;
									}

									@Override
									public void onDragStart(AjaxRequestTarget target, int top, int left) {
										super.onDragStart(target, top, left);

										target.appendJavaScript(String.format("$('#%s').css('width: 5px; height: 5px; transform: none;');", marker.getMarkupId()));
									}

									@Override
									public void onDragStop(AjaxRequestTarget target,
														   int top, int left) {
										super.onDragStop(target, top, left);

										final int newX = (int) (left / previewImage.getFactor());
										final int newY = (int) (top / previewImage.getFactor());

										AreaMarker areaMarker = item.getModelObject();
										areaMarker.visit(new AreaMarkerVisitor<Void>() {
											@Override
											public Void visit(
													@Nonnull
															CircleMarker marker) {

												markerService
														.update(marker, marker.getColor(),
																newX,
																newY,
																marker.getExtent());

												return null;
											}

											@Override
											public Void visit(
													@Nonnull
															ConeMarker marker) {
												markerService
														.update(marker, marker.getColor(), newX,
																newY,
																marker.getExtent(),
																marker.getTheta());

												return null;
											}

											@Override
											public Void visit(
													@Nonnull
															CubeMarker marker) {
												markerService
														.update(marker, marker.getColor(), newX,
																newY,
																marker.getExtent());

												return null;
											}

											@Override
											public Void visit(
													@Nonnull
															LineMarker marker) {
												markerService
														.update(marker, marker.getColor(), newX,
																newY,
																marker.getExtent(), marker
																		.getTheta());

												return null;
											}
										});


									}
								}));

						item.add(marker);
					}
				};

		previewImage.add(mapMarkerView);
		previewImage.setOutputMarkupId(true);

		add(previewImage);
	}

	public abstract void replaceMe(AjaxRequestTarget target, WebMarkupContainer replacement);


	private static class MarkerStyleModel<T extends AreaMarker> extends
			LoadableDetachableModel<String> {

		private final IModel<T> markerModel;

		private final double factor;

		private SerializableBiFunction<T, Double, Long> width;

		private SerializableBiFunction<T, Double, Long> height;

		private SerializableBiFunction<T, Double, Long> x;

		private SerializableBiFunction<T, Double, Long> y;

		private SerializableBiFunction<T, Double, Double> opacity;

		private SerializableBiFunction<T, Double, Long> borderRadiusPercent;

		private SerializableBiFunction<T, Double, Long> borderRadiusPX;

		private SerializableBiFunction<T, Double, String> backgroundColor;

		private SerializableBiFunction<T, Double, String> transform;

		private SerializableBiFunction<T, Double, String> borderTop;

		private SerializableBiFunction<T, Double, String> borderLeft;

		private SerializableBiFunction<T, Double, String> borderRight;

		private SerializableBiFunction<T, Double, String> borderBottom;


		private MarkerStyleModel(T marker, double factor) {
			this.markerModel = ModelMaker.wrap(marker);
			this.factor = factor;
		}

		public MarkerStyleModel<T> setWidth(
				SerializableBiFunction<T, Double, Long> width) {
			this.width = width;
			return this;
		}

		public MarkerStyleModel<T> setHeight(
				SerializableBiFunction<T, Double, Long> height) {
			this.height = height;
			return this;
		}

		public MarkerStyleModel<T> setX(
				SerializableBiFunction<T, Double, Long> x) {
			this.x = x;
			return this;
		}

		public MarkerStyleModel<T> setY(
				SerializableBiFunction<T, Double, Long> y) {
			this.y = y;
			return this;
		}

		public MarkerStyleModel<T> setBorderRadiusPercent(
				SerializableBiFunction<T, Double, Long> borderRadiusPercent) {
			this.borderRadiusPercent = borderRadiusPercent;
			return this;
		}

		public MarkerStyleModel<T> setBorderRadiusPX(
				SerializableBiFunction<T, Double, Long> borderRadiusPX) {
			this.borderRadiusPX = borderRadiusPX;
			return this;
		}

		public MarkerStyleModel<T> setOpacity(
				SerializableBiFunction<T, Double, Double> opacity) {
			this.opacity = opacity;
			return this;
		}

		public MarkerStyleModel<T> setBackgroundColor(
				SerializableBiFunction<T, Double, String> backgroundColor) {
			this.backgroundColor = backgroundColor;
			return this;
		}

		public MarkerStyleModel<T> setTransform(
				SerializableBiFunction<T, Double, String> transform) {
			this.transform = transform;
			return this;
		}

		public MarkerStyleModel<T> setBorderTop(
				SerializableBiFunction<T, Double, String> borderTop) {
			this.borderTop = borderTop;
			return this;
		}

		public MarkerStyleModel<T> setBorderLeft(
				SerializableBiFunction<T, Double, String> borderLeft) {
			this.borderLeft = borderLeft;
			return this;
		}

		public MarkerStyleModel<T> setBorderRight(
				SerializableBiFunction<T, Double, String> borderRight) {
			this.borderRight = borderRight;
			return this;
		}

		public MarkerStyleModel<T> setBorderBottom(
				SerializableBiFunction<T, Double, String> borderBottom) {
			this.borderBottom = borderBottom;
			return this;
		}

		@Override
		protected String load() {
			T marker = markerModel.getObject();
			StringBuilder sb = new StringBuilder();
			sb.append("position: absolute;");
			apply(sb, factor, marker, "width", width, null, "px");
			apply(sb, factor, marker, "height", height, null, "px");
			apply(sb, factor, marker, "left", x, null, "px");
			apply(sb, factor, marker, "top", y, null, "px");
			apply(sb, factor, marker, "border-radius", borderRadiusPercent, null, "%");
			apply(sb, factor, marker, "border-radius", borderRadiusPX, null, "px");
			apply(sb, factor, marker, "opacity", opacity, null, null);
			apply(sb, factor, marker, "border-top", borderTop, null, null);
			apply(sb, factor, marker, "border-left", borderLeft, null, null);
			apply(sb, factor, marker, "border-right", borderRight, null, null);
			apply(sb, factor, marker, "border-bottom", borderBottom, null, null);
			apply(sb, factor, marker, "background-color", backgroundColor, "#", null);
			apply(sb, factor, marker, "transform", transform, null, null);

			return sb.toString();
		}

		private <U extends Serializable> void apply(
				@Nonnull
						StringBuilder builder, double factor,
				@Nonnull
						T marker,
				@Nonnull
						String field,
				@Nullable
						SerializableBiFunction<T, Double, U> input,
				@Nullable
						String prefix,
				@Nullable
						String unit

		) {
			if (input != null) {
				U value = input.apply(marker, factor);
				builder.append(field).append(": ");
				if (prefix != null && !value.toString().startsWith(prefix)) {
					builder.append(prefix);
				}
				builder.append(value);
				if (unit != null) {
					builder.append(unit);
				}
				builder.append("; ");
			}
		}

		@Override
		protected void onDetach() {
			super.onDetach();
			markerModel.detach();
		}

	}
}