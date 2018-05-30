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
import com.jeroensteenbeeke.topiroll.beholder.web.components.MarkerStyleModel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;

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
												.setX((m, factor) -> Math.round(factor * (m.getOffsetX()+wh)))
												.setY((m, factor) -> Math.round(factor * (m.getOffsetY()+wh)))
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


										return new MarkerStyleModel<>(marker, previewImage.getFactor())
												.setX((m, factor) -> Math.round((m.getOffsetX()+wh) * factor))
												.setY((m, factor) -> Math.round((m.getOffsetY()+wh) * factor))
												.setWidth((m, factor) -> 0L)
												.setHeight((m, factor) -> 0L)
												.setBorderTop((m, factor) -> "1px solid transparent")
												.setBorderRight((m, factor) -> String.format("%fpx solid #%s",
														wh * factor, marker.getColor()))
												.setBorderBottom((m, factor) -> "5px solid transparent")
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
														.update(marker, marker.getColor(), newX-wh,
																newY-wh,
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
														.update(marker, marker.getColor(), newX-wh,
																newY-wh,
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


}
