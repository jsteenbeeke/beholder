package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.combat;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableAdapter;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableBehavior;
import com.jeroensteenbeeke.hyperion.data.DomainObject;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.BootstrapBasePage;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.beans.MarkerService;
import com.jeroensteenbeeke.topiroll.beholder.dao.InitiativeParticipantDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.InitiativeParticipantFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.visitor.AreaMarkerVisitor;
import com.jeroensteenbeeke.topiroll.beholder.web.BeholderSession;
import com.jeroensteenbeeke.topiroll.beholder.web.components.AbstractMapPreview;
import com.jeroensteenbeeke.topiroll.beholder.web.components.MarkerStyleModel;
import com.jeroensteenbeeke.topiroll.beholder.web.components.OnClickBehavior;
import com.jeroensteenbeeke.topiroll.beholder.web.components.combat.*;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.HomePage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.ControlViewPage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.UrlUtils;
import org.apache.wicket.request.cycle.RequestCycle;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.awt.Point;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class CombatControllerPage extends BootstrapBasePage implements CombatModeCallback {
	private static final String MODAL_ID = "modal";
	public static final String MARKER_ID = "marker";
	public static final String TOKEN_ID = "token";
	public static final String PARTICIPANT_ID = "participant";
	private final SortedMap<Integer, Integer> calculatedWidths;
	private final TokenStatusPanel tokenStatusPanel;
	private final MapOptionsPanel mapOptionsPanel;
	private final MarkerStatusPanel markerStatusPanel;

	private Component modal;

	private IModel<TokenInstance> selectedToken = Model.of();

	private IModel<AreaMarker> selectedMarker = Model.of();

	private final IModel<ScaledMap> mapModel;

	private final IModel<MapView> viewModel;

	private Point clickedLocation = null;

	private Point previousClickedLocation = null;

	@Inject
	private MapService mapService;

	@Inject
	private InitiativeParticipantDAO participantDAO;

	private final AbstractMapPreview preview;

	private boolean disableClickListener = false;

	public CombatControllerPage(MapView view) {
		super("Combat Mode");

		if (BeholderSession.get().getUser() == null) {
			BeholderSession.get().invalidate();
			throw new RestartResponseAtInterceptPageException(HomePage.class);
		}

		viewModel = ModelMaker.wrap(view);

		ScaledMap map = view.getSelectedMap();

		if (map == null) {
			throw new RestartResponseAtInterceptPageException(ControlViewPage.class);
		}

		final double displayFactor = map.getDisplayFactor(view);
		int desiredWidth = (int) (displayFactor * map.getBasicWidth());

		preview = new AbstractMapPreview("preview", map, desiredWidth) {
			@Override
			protected void addOnDomReadyJavaScript(String canvasId, StringBuilder js,
												   double factor) {

			}

		};

		preview.add(new InitiativePanel("initiative", view));
		tokenStatusPanel = new TokenStatusPanel("tokenStatus", this);
		mapOptionsPanel = new MapOptionsPanel("mapOptions", view, this);
		markerStatusPanel = new MarkerStatusPanel("markerStatus", this);
		preview.add(tokenStatusPanel.setVisible(false));
		preview.add(mapOptionsPanel.setVisible(false));
		preview.add(markerStatusPanel.setVisible(false));

		preview.add(new OnClickBehavior() {
			@Override
			protected void onClick(AjaxRequestTarget target, ClickEvent event) {
				super.onClick(target, event);

				if (!disableClickListener) {
					previousClickedLocation = clickedLocation;
					clickedLocation = new Point((int) (event.getOffsetLeft() / displayFactor), (int)

							(event
									.getOffsetTop() / displayFactor));
					selectedMarker = Model.of();
					selectedToken = Model.of();

					mapOptionsPanel.setVisible(true);
					tokenStatusPanel.setVisible(false);
					markerStatusPanel.setVisible(false);
					target.add(mapOptionsPanel, tokenStatusPanel, markerStatusPanel);
				}
			}
		});

		this.calculatedWidths = new TreeMap<>();

		mapModel = ModelMaker.wrap(map);

		IModel<List<TokenInstance>> tokenModel = new LoadableDetachableModel<List<TokenInstance>>() {
			@Override
			protected List<TokenInstance> load() {
				return mapModel.getObject().getTokens().stream()
						.filter(t -> t.getCurrentHitpoints() == null || t.getCurrentHitpoints() > 0).collect(Collectors.toList());
			}
		};

		preview.add(new ListView<TokenInstance>("tokens", tokenModel) {
			@Override
			protected void populateItem(ListItem<TokenInstance> item) {
				TokenInstance instance = item.getModelObject();

				int squareSize = map.getSquareSize();

				int wh = squareSize
						* instance.getDefinition().getDiameterInSquares();

				if (!calculatedWidths.containsKey(item.getIndex())) {
					calculatedWidths.put(item.getIndex(), wh);
				}

				Label image = new Label(TOKEN_ID, instance.getLabel());
				image.add(AttributeModifier.replace("style",
						new LoadableDetachableModel<String>() {
							private static final long serialVersionUID = 1L;

							@Override
							protected String load() {
								TokenInstance i = item.getModelObject();
								int left = i.getOffsetX();
								int top = i.getOffsetY();

								for (int v : calculatedWidths.headMap(item.getIndex())
										.values()) {
									left = left - v;
								}

								left = preview.translateToScaledImageSize(left);
								top = preview.translateToScaledImageSize(top);

								int actualWH = preview.translateToScaledImageSize(wh);

								return String.format(
										"z-index: %6$d; left: %1$dpx; top: %2$dpx; max-width: " +
												"%3$dpx !important; " +
												"width: %3$dpx; height: %3$dpx; max-height: %3$dpx " +
												"!important; background-size: %3$dpx %3$dpx; border-radius: 100%%; border: 1px " +
												"solid #%4$s; background-image: url('%5$s'); " +
												"display: table-cell; vertical-align: bottom; " +
												"color: #cccccc; text-align: center;",
										left, top, actualWH, i
												.getBorderType().toHexColor(),
										UrlUtils.rewriteToContextRelative(String.format("images/token/%d",
												instance.getDefinition().getId()), RequestCycle
												.get()),
										2+item.getIndex()
										);
							}

						}));
				image.add(AttributeModifier.replace("title", new LoadableDetachableModel<String>() {
					@Override
					protected String load() {
						TokenInstance instance = item.getModelObject();
						return Optional.ofNullable(instance).filter(i -> i.getCurrentHitpoints() != null && i.getMaxHitpoints() != null).map(
								i -> 100 * i.getCurrentHitpoints() / i.getMaxHitpoints()
						).map(p -> String.format("%s (%d%% health)", instance.getBadge(), p)).orElse(instance.getBadge());
					}
				}));

				Options draggableOptions = new Options();
				draggableOptions.set("opacity", "0.5");
				draggableOptions.set("containment", Options.asString("parent"));
				image.add(new DraggableBehavior(draggableOptions,
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

//								for (int v : calculatedWidths
//										.headMap(item.getIndex()).values()) {
//									x = x + v;
//								}

								x = preview.translateToRealImageSize(x);
								int y = preview.translateToRealImageSize(top + 1);

								mapService.updateTokenLocation(
										item.getModelObject(), x, y);

								redrawMap(target);
							}
						}));

				image.add(new OnClickBehavior() {
					@Override
					protected void onClick(AjaxRequestTarget target, ClickEvent event) {
						super.onClick(target, event);

						selectedToken = ModelMaker.wrap(item.getModelObject());
						clickedLocation = null;
						selectedMarker = Model.of();

						mapOptionsPanel.setVisible(false);
						tokenStatusPanel.setVisible(true);
						markerStatusPanel.setVisible(false);
						target.add(tokenStatusPanel, mapOptionsPanel, markerStatusPanel);
					}
				}.withoutPropagation());

				item.add(image);

			}
		});

		IModel<List<AreaMarker>> markerModel = new LoadableDetachableModel<List<AreaMarker>>() {
			@Override
			protected List<AreaMarker> load() {
				return viewModel.getObject().getMarkers();
			}
		};


		preview.add(new ListView<AreaMarker>("markers", markerModel) {
			@Inject
			private MarkerService markerService;

			@Override
			protected void populateItem(ListItem<AreaMarker> item) {
				AreaMarker areaMarker = item.getModelObject();

				int squareSize = map.getSquareSize();

				int wh = squareSize * areaMarker.getExtent() / 5;

				MarkerStyleModel<?> markerStyleModel = areaMarker
						.visit(new AreaMarkerVisitor<MarkerStyleModel<?>>() {
							@Override
							public MarkerStyleModel<?> visit(
									@Nonnull
											CircleMarker marker) {
								return new MarkerStyleModel<>(marker, displayFactor)
										.setX((m, factor) -> Math.round(factor * m.getOffsetX()))
										.setY((m, factor) -> Math.round(factor * m.getOffsetY()))
										.setBackgroundColor((m, factor) -> marker.getColor())
										.setWidth((m, factor) -> Math.round(wh * factor * 2))
										.setHeight((m, factor) -> Math.round(wh * factor * 2))
										.setOpacity((m, factor) -> 0.5)
										.setBorderRadiusPercent((m, factor) -> 100L)
										.setTransform((m, factor) -> String.format
												("translate(%1$dpx,%1$dpx)", -wh));
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


								return new MarkerStyleModel<>(marker, displayFactor).setX((m, factor) -> Math.round((m.getOffsetX()) * factor)).setY((m, factor) -> Math.round(m.getOffsetY() * factor)).setWidth((m, factor) -> 0L)
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
								return new MarkerStyleModel<>(marker, displayFactor)
										.setX((m, factor) -> Math.round(factor * m.getOffsetX()))
										.setY((m, factor) -> Math.round(factor * m.getOffsetY()))
										.setWidth((m, factor) -> Math.round(wh * factor))
										.setHeight((m, factor) -> Math.round(wh * factor))
										.setBackgroundColor((m, factor) -> marker.getColor())
										.setOpacity((m, factor) -> 0.5)
										.setTransform((m, factor) -> String.format
												("translate(%1$dpx,%1$dpx)", -wh / 2));
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


								return new MarkerStyleModel<>(marker, displayFactor).setX((m, factor) -> Math.round((m.getOffsetX()) * factor)).setY((m, factor) -> Math.round(m.getOffsetY() * factor)).setWidth((m, factor) -> 0L)
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

								target.appendJavaScript(String.format("$('#%s').css('width: 5px; height: 5px; transform: none;');", item.get("marker").getMarkupId()));
							}

							@Override
							public void onDragStop(AjaxRequestTarget target,
												   int top, int left) {
								super.onDragStop(target, top, left);

								final int newX = (int) (left / displayFactor);
								final int newY = (int) (top / displayFactor);

								item.detach();
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
				marker.add(new OnClickBehavior() {
					@Override
					protected void onClick(AjaxRequestTarget target, ClickEvent event) {
						super.onClick(target, event);

						selectedToken = Model.of();
						clickedLocation = null;
						selectedMarker = ModelMaker.wrap(item.getModelObject());;

						mapOptionsPanel.setVisible(false);
						tokenStatusPanel.setVisible(false);
						markerStatusPanel.setVisible(true);
						target.add(tokenStatusPanel, mapOptionsPanel, markerStatusPanel);
					}
				}.withoutPropagation());

				item.add(marker);
			}
		});

		IModel<List<InitiativeParticipant>> participantModel = new LoadableDetachableModel<List<InitiativeParticipant>>() {
			@Override
			protected List<InitiativeParticipant> load() {
				InitiativeParticipantFilter initFilter = new InitiativeParticipantFilter();
				initFilter.player(true);
				initFilter.view(viewModel.getObject());
				return participantDAO.findByFilter(initFilter);
			}
		};

		preview.add(new ListView<InitiativeParticipant>("participants",
				participantModel) {

			@Override
			protected void populateItem(ListItem<InitiativeParticipant> item) {
				InitiativeParticipant participant = item.getModelObject();
				int wh = map.getSquareSize();

				Label image = new Label(PARTICIPANT_ID, participant.getName());
				image.add(AttributeModifier.replace("style",
						new LoadableDetachableModel<String>() {
							private static final long serialVersionUID = 1L;

							@Override
							protected String load() {
								InitiativeParticipant participant = item.getModelObject();

								int left = Optional.ofNullable(participant.getOffsetX())
										.map(Math::abs)
										.orElse((int) (map.getBasicWidth() * displayFactor / 2));
								int top = Optional.ofNullable(participant.getOffsetY())
										.map(Math::abs)
										.orElse((int) (map.getBasicHeight() * displayFactor / 2)) - 1;

								left = preview.translateToScaledImageSize(left);
								top = preview.translateToScaledImageSize(top);

								int actualWH = preview.translateToScaledImageSize(wh);


								return String.format(
										"left: %1$dpx; top: %2$dpx; max-width: %3$dpx !important;" +
												" " +
												"width: %3$dpx; height: %3$dpx; max-height: %3$dpx " +
												"!important; border-radius: 100%%; border: 1px " +
												"solid" +
												" " +
												"#00ff00; text-align: center; word-break: " +
												"break-all; vertical-align: middle; display: " +
												"table-cell; color: #cccccc; " +
												"background-image: url('%4$s'); background-size: " +
												"%3$dpx %3$dpx;",
										left, top, actualWH,
										UrlUtils.rewriteToContextRelative("img/player.png",
												RequestCycle.get()));
							}

						}));
				image.add(AttributeModifier.replace("title", participant.getName()));


				Options draggableOptions = new Options();
				draggableOptions.set("opacity", "0.5");
				draggableOptions.set("containment", Options.asString("parent"));
				image.add(new

						DraggableBehavior(draggableOptions,
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


								// TODO: Service method?
								InitiativeParticipant participant = item.getModelObject();
								participant.setOffsetX((int) (left / displayFactor));
								participant.setOffsetY((int) (top / displayFactor));
								participantDAO.update(participant);

							}
						})).add(new OnClickBehavior() {
											@Override
											protected void onClick(AjaxRequestTarget target,
																   ClickEvent event) {
												super.onClick(target, event);

												// TODO: What happens when clicking a player marker
											}
										});

				item.add(image);
			}
		});

		preview.add(new Link<MapView>("back", ModelMaker.wrap(view))
		{
			@Override
			public void onClick() {
				setResponsePage(new ControlViewPage(getModelObject()));
			}
		});

		add(preview);

		add(modal = new WebMarkupContainer(MODAL_ID));
		modal.setOutputMarkupPlaceholderTag(true);
	}

	@Override
	public void redrawMap(AjaxRequestTarget target) {
		clickedLocation = null;
		previousClickedLocation = null;
		preview.refresh(target);
	}

	@Override
	public TokenInstance getSelectedToken() {
		return selectedToken.getObject();
	}

	@Override
	public AreaMarker getSelectedMarker() {
		return selectedMarker.getObject();
	}

	@Override
	public Point getClickedLocation() {
		return clickedLocation;
	}

	@Override
	public Point getPreviousClickedLocation() {
		return previousClickedLocation;
	}

	@Override
	public <T extends DomainObject> void createModalWindow(
			@Nonnull
					AjaxRequestTarget target,
			@Nonnull
					PanelConstructor<T> constructor,
			@Nonnull
					T object) {
		disableClickListener = true;
		Component oldModal = modal;
		oldModal.replaceWith(modal = constructor.apply(MODAL_ID, object, this));
		target.add(modal);
		target.appendJavaScript("$('#combat-modal').modal('show');");
	}

	@Override
	public void removeModal(AjaxRequestTarget target) {
		Component oldModal = modal;
		oldModal.replaceWith(modal = new WebMarkupContainer(MODAL_ID).setOutputMarkupPlaceholderTag(true).setVisible(false));
		target.add(modal);
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		mapModel.detach();
		viewModel.detach();
		selectedMarker.detach();
		selectedToken.detach();

		disableClickListener = false;
	}
}
