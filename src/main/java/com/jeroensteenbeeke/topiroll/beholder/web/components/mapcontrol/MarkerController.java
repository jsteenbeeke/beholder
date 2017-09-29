/**
 * This file is part of Beholder
 * (C) 2016 Jeroen Steenbeeke
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.jeroensteenbeeke.topiroll.beholder.web.components.mapcontrol;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableAdapter;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableBehavior;
import com.jeroensteenbeeke.hyperion.ducktape.web.components.TypedPanel;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.AjaxIconLink;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.GlyphIcon;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.hyperion.util.ImageUtil;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.beans.MarkerService;
import com.jeroensteenbeeke.topiroll.beholder.dao.AreaMarkerDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.AreaMarkerFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.visitor.AreaMarkerVisitor;
import com.jeroensteenbeeke.topiroll.beholder.web.components.AbstractMapPreview;
import com.jeroensteenbeeke.topiroll.beholder.web.components.ImageContainer;
import com.jeroensteenbeeke.topiroll.beholder.web.components.mapcontrol.markers
		.SelectMarkerTypeController;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.UrlUtils;
import org.apache.wicket.request.cycle.RequestCycle;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.management.Attribute;
import java.awt.*;
import java.util.SortedMap;
import java.util.TreeMap;

public abstract class MarkerController extends TypedPanel<MapView> implements IClickListener {

	private static final long serialVersionUID = 1L;

	private static final String MARKER_ID = "marker";

	private final SortedMap<Integer, Integer> calculatedWidths;

	@Inject
	private AreaMarkerDAO markerDAO;

	@Inject
	private MarkerService markerService;

	public MarkerController(String id, MapView view) {
		super(id, ModelMaker.wrap(view));

		ScaledMap map = view.getSelectedMap();

		AreaMarkerFilter filter = new AreaMarkerFilter();
		filter.view().set(view);

		add(new DataView<AreaMarker>("markers", FilterDataProvider.of(filter, markerDAO)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<AreaMarker> item) {
				AreaMarker marker = item.getModelObject();

				item.add(marker.createPanel("marker"));
				item.add(new AjaxIconLink<AreaMarker>("delete", item.getModel(), GlyphIcon.trash) {
					private static final long serialVersionUID = 1L;

					@Inject
					private MapService mapService;


					@Override
					public void onClick(AjaxRequestTarget target) {
						AreaMarker marker = item.getModelObject();
						MapView markerView = marker.getView();
						markerDAO.delete(marker);
						mapService.refreshView(markerView);

						replaceMe(target, null);
					}
				});
			}
		});

		this.calculatedWidths = new TreeMap<>();


		AbstractMapPreview previewImage = new AbstractMapPreview("map",map) {
			@Override
			protected void addOnDomReadyJavaScript(String canvasId, StringBuilder js, double factor) {

			}
		};

		ListView<AreaMarker> mapMarkerView =
				new ListView<AreaMarker>("mapmarkers", ModelMaker.wrapList(view.getMarkers())) {
					@Override
					protected void populateItem(ListItem<AreaMarker> item) {
						AreaMarker areaMarker = item.getModelObject();

						int squareSize = map.getSquareSize();

						int wh = squareSize * areaMarker.getExtent() / 5;

						calculatedWidths.put(item.getIndex(), wh + 4);

						WebMarkupContainer marker = areaMarker.visit(new AreaMarkerVisitor<WebMarkupContainer>() {
							@Override
							public WebMarkupContainer visit(@Nonnull CircleMarker marker) {
								WebMarkupContainer container = new WebMarkupContainer(MARKER_ID);
								container.add(AttributeModifier
										.replace("style", new MarkerStyleModel<CircleMarker>(marker) {
											private static final long serialVersionUID = 1L;

											@Override
											protected String onLoad(CircleMarker marker) {
												return
														String.format(
																"width: %1$dpx; height: %1$dpx; border-radius: " +
																		"100%%; " +
																		"background-color: " +
																		"#%2$s; opacity: 0.5;" +
																		" left: %3$dpx; top: %4$dpx; display: block;",
																wh * 2, marker.getColor(), marker.getOffsetX(),
																marker.getOffsetY());
											}
										}));
								return container;
							}

							@Override
							public WebMarkupContainer visit(@Nonnull ConeMarker marker) {

								WebMarkupContainer container = new WebMarkupContainer(MARKER_ID);
								container.add(AttributeModifier
										.replace("style", new MarkerStyleModel<ConeMarker>(marker) {
											private static final long serialVersionUID = 1L;

											@Override
											protected String onLoad(ConeMarker marker) {
												return String
														.format("left: %4$dpx; top: %5$dpx; width: 0; height: 0; " +
																		"border-left: %1$dpx solid transparent;\n" +
																		"\t\t\t\tborder-right: %1$dpx solid " +
																		"transparent;\n" +
																		"\t\t\t\tborder-top: %1$dpx solid #%2$s;\n" +
																		"\t\t\t\t-moz-border-radius: 50%%;\n" +
																		"\t\t\t\t-webkit-border-radius: 50%%;\n" +
																		"\t\t\t\tborder-radius: 50%%;\n" +
																		"\t\t\t\topacity: 0.5;\n" +
																		"\t\t\t\ttransform: rotate(%3$fdeg);",
																wh, marker.getColor(),
																Math.toDegrees(marker.getTheta())-90,
																marker.getOffsetX(),
																marker.getOffsetY()
														);
											}
										}));
								return container;
							}

							@Override
							public WebMarkupContainer visit(@Nonnull CubeMarker marker) {
								WebMarkupContainer container = new WebMarkupContainer(MARKER_ID);
								container.add(AttributeModifier.replace("style",
										new MarkerStyleModel<CubeMarker>(marker) {
											private static final long serialVersionUID = 1L;

											@Override
											protected String onLoad(CubeMarker marker) {
												return String.format(
														"left: %3$dpx; top: %4$dpx; width: %1$dpx; height: %1$dpx; " +
																"background-color: #%2$s; display: block; position: relative;",
														wh, marker.getColor(), marker.getOffsetX(),
														marker.getOffsetY());
											}
										}));
								return container;

							}

							@Override
							public WebMarkupContainer visit(@Nonnull LineMarker marker) {
								WebMarkupContainer container = new WebMarkupContainer(MARKER_ID);
								container.add(AttributeModifier.replace("style",
										new MarkerStyleModel<LineMarker>(marker) {
											private static final long serialVersionUID = 1L;

											@Override
											protected String onLoad(LineMarker marker) {
												return String.format(
														"left: %4$dpx; top: %5$dpx; width: 1px; height: %1$dpx; background-color: #%2$s; " +
																"transform: rotate(%3$fdeg); ",
														wh, marker.getColor(), Math.toDegrees(marker.getTheta()),
														marker.getOffsetX(),
														marker.getOffsetY());
											}
										}));
								return container;
							}
						});

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
									public void onDragStop(AjaxRequestTarget target,
														   int top, int left) {
										super.onDragStop(target, top, left);

										int x = left;

										for (int v : calculatedWidths
												.headMap(item.getIndex()).values()) {
											x = x + v;
										}

										final int newX = x;

										AreaMarker areaMarker = item.getModelObject();
										areaMarker.visit(new AreaMarkerVisitor<Void>() {
											@Override
											public Void visit(@Nonnull CircleMarker marker) {
												markerService.update(marker, marker.getColor(), newX, top,
														marker.getExtent());

												return null;
											}

											@Override
											public Void visit(@Nonnull ConeMarker marker) {
												markerService
														.update(marker, marker.getColor(), newX, top, marker
																		.getTheta(),
																marker.getExtent());

												return null;
											}

											@Override
											public Void visit(@Nonnull CubeMarker marker) {
												markerService.update(marker, marker.getColor(), newX, top,
														marker.getExtent());

												return null;
											}

											@Override
											public Void visit(@Nonnull LineMarker marker) {
												markerService
														.update(marker, marker.getColor(), newX, top, marker
																		.getTheta(),
																marker.getExtent());

												return null;
											}
										});

										replaceMe(target,
												new MarkerController(id, MarkerController.this.getModelObject()) {
													@Override
													public void replaceMe(AjaxRequestTarget target,
																		  WebMarkupContainer replacement) {
														MarkerController.this.replaceMe(target, replacement);
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

	@Override
	public void onClick(AjaxRequestTarget target, ScaledMap map, int x, int y) {
		replaceMe(target, new SelectMarkerTypeController(getId(), getModelObject(), x, y) {
			@Override
			public void replaceMe(AjaxRequestTarget target,
								  WebMarkupContainer replacement) {
				MarkerController.this.replaceMe(target, replacement);
			}
		});
	}

	public abstract void replaceMe(AjaxRequestTarget target, WebMarkupContainer replacement);

	private abstract static class MarkerStyleModel<T extends AreaMarker> extends LoadableDetachableModel<String> {
		private final IModel<T> markerModel;

		private MarkerStyleModel(T marker) {
			this.markerModel = ModelMaker.wrap(marker);
		}

		protected abstract String onLoad(T marker);

		@Override
		protected String load() {
			return onLoad(markerModel.getObject());
		}

		@Override
		protected void onDetach() {
			super.onDetach();
			markerModel.detach();
		}

	}
}
