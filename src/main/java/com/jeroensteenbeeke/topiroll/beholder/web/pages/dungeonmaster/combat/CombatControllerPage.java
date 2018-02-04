package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.combat;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableAdapter;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableBehavior;
import com.jeroensteenbeeke.hyperion.data.DomainObject;
import com.jeroensteenbeeke.hyperion.ducktape.web.util.Components;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.BootstrapBasePage;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
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
import com.jeroensteenbeeke.topiroll.beholder.web.components.combat.CombatModeCallback;
import com.jeroensteenbeeke.topiroll.beholder.web.components.combat.InitiativePanel;
import com.jeroensteenbeeke.topiroll.beholder.web.components.combat.MapOptionsPanel;
import com.jeroensteenbeeke.topiroll.beholder.web.components.combat.TokenStatusPanel;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.HomePage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.ControlViewPage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.awt.*;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

public class CombatControllerPage extends BootstrapBasePage implements CombatModeCallback {
	private static final String MODAL_ID = "modal";
	public static final String MARKER_ID = "marker";
	public static final String TOKEN_ID = "token";
	public static final String PARTICIPANT_ID = "participant";
	private final SortedMap<Integer, Integer> calculatedWidths;
	private final TokenStatusPanel tokenStatusPanel;
	private final MapOptionsPanel mapOptionsPanel;

	private final ListView<TokenInstance> tokenView;

	private final ListView<AreaMarker> markerView;

	private final ListView<InitiativeParticipant> participantsView;

	private Component modal;

	private IModel<TokenInstance> selectedToken = Model.of();

	private IModel<AreaMarker> selectedMarker = Model.of();

	private Point clickedLocation = null;

	@Inject
	private MapService mapService;

	@Inject
	private InitiativeParticipantDAO participantDAO;

	private final AbstractMapPreview preview;

	public CombatControllerPage(MapView view) {
		super("Combat Mode");

		if (BeholderSession.get().getUser() == null) {
			BeholderSession.get().invalidate();
			throw new RestartResponseAtInterceptPageException(HomePage.class);
		}

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
		preview.add(tokenStatusPanel.setVisible(false));
		preview.add(mapOptionsPanel.setVisible(false));

		preview.add(new OnClickBehavior() {
			@Override
			protected void onClick(AjaxRequestTarget target, ClickEvent event) {
				super.onClick(target, event);

				clickedLocation = new Point(event.getOffsetLeft(), event.getOffsetTop());
				selectedMarker = Model.of();
				selectedToken = Model.of();

				mapOptionsPanel.setVisible(true);
				tokenStatusPanel.setVisible(false);
				target.add(mapOptionsPanel);
			}
		});

		this.calculatedWidths = new TreeMap<>();
		preview.add(tokenView = new ListView<TokenInstance>("tokens", ModelMaker.wrapList(map.getTokens(),
				false)) {
			@Override
			protected void populateItem(ListItem<TokenInstance> item) {
				TokenInstance instance = item.getModelObject();

				int squareSize = map.getSquareSize();

				int wh = squareSize
						* instance.getDefinition().getDiameterInSquares();

				calculatedWidths.put(item.getIndex(), wh + 4);

				ContextImage image = new ContextImage("token",
						String.format("tokens/%d",
								instance.getDefinition().getId()));
				image.add(AttributeModifier.replace("style",
						new LoadableDetachableModel<String>() {
							private static final long serialVersionUID = 1L;

							@Override
							protected String load() {
								int index = item.getIndex();
								TokenInstance i = item.getModelObject();
								int left = i.getOffsetX();
								int top = i.getOffsetY() - 1;

								for (int v : calculatedWidths.headMap(index)
										.values()) {
									left = left - v;
								}

								left = preview.translateToScaledImageSize(left);
								top = preview.translateToScaledImageSize(top);

								int actualWH = preview.translateToScaledImageSize(wh);

								String tokenBorderColor = instance.getBorderType().toHexColor();

								return String.format(
										"left: %dpx; top: %dpx; max-width: %dpx !important; " +
												"width: %dpx; height: %dpx; max-height: %dpx " +
												"!important; border-radius: 100%%; border: 1px " +
												"solid" +
												" " +
												"#%s",
										left, top, actualWH, actualWH, actualWH, actualWH,
										tokenBorderColor);
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

								for (int v : calculatedWidths
										.headMap(item.getIndex()).values()) {
									x = x + v;
								}

								x = preview.translateToRealImageSize(x);
								int y = preview.translateToRealImageSize(top + 1);

								mapService.updateTokenLocation(
										item.getModelObject(), x, y);

								target.add(preview);
							}
						}));

				image.add(new OnClickBehavior() {
					@Override
					protected void onClick(AjaxRequestTarget target, ClickEvent event) {
						super.onClick(target, event);

						selectedToken = ModelMaker.wrap(item.getModelObject());
						clickedLocation = null;
						selectedMarker = Model.of();

						tokenStatusPanel.setVisible(true);
						target.add(tokenStatusPanel);
					}
				});

				item.add(image);

			}
		});


		preview.add(markerView = new ListView<AreaMarker>("markers", ModelMaker.wrapList(view.getMarkers(), false)) {
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
				WebMarkupContainer marker = new WebMarkupContainer("marker");
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

				item.add(marker);
			}
		});

		InitiativeParticipantFilter initFilter = new InitiativeParticipantFilter();
		initFilter.player(true);

		preview.add(participantsView = new ListView<InitiativeParticipant>("participants", ModelMaker.wrapList(participantDAO.findByFilter(initFilter), false)) {

			@Override
			protected void populateItem(ListItem<InitiativeParticipant> item) {
				InitiativeParticipant participant = item.getModelObject();
				int wh = map.getSquareSize();

				calculatedWidths.put(item.getIndex(), wh + 4);

				ContextImage image = new ContextImage(PARTICIPANT_ID, "img/player.png");
				image.add(AttributeModifier.replace("style",
						new LoadableDetachableModel<String>() {
							private static final long serialVersionUID = 1L;

							@Override
							protected String load() {
								item.detach();
								InitiativeParticipant participant = item.getModelObject();

								int index = item.getIndex();
								int left = Optional.ofNullable(participant.getOffsetX())
										.map(Math::abs)
										.orElse((int) (map.getBasicWidth() * displayFactor / 2));
								int top = Optional.ofNullable(participant.getOffsetY())
										.map(Math::abs)
										.orElse((int) (map.getBasicHeight() * displayFactor / 2)) - 1;
								;

								left = preview.translateToScaledImageSize(left);
								top = preview.translateToScaledImageSize(top);

								int actualWH = preview.translateToScaledImageSize(wh);


								return String.format(
										"left: %dpx; top: %dpx; max-width: %dpx !important; " +
												"width: %dpx; height: %dpx; max-height: %dpx " +
												"!important; border-radius: 100%%; border: 1px " +
												"solid" +
												" " +
												"#00ff00;",
										left, top, actualWH, actualWH, actualWH, actualWH);
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

								int x = left;

								for (int v : calculatedWidths
										.headMap(item.getIndex()).values()) {
									x = x + v;
								}

								// TODO: Update fields
							}
						}));

				image.add(new

								  OnClickBehavior() {
									  @Override
									  protected void onClick(AjaxRequestTarget target, ClickEvent event) {
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
		Components.forEach(tokenView, i -> target.add(i.get(TOKEN_ID)));
		Components.forEach(markerView, i -> target.add(i.get(MARKER_ID)));
		Components.forEach(participantsView, i -> target.add(i.get(PARTICIPANT_ID)));
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
	public <T extends DomainObject> void createModalWindow(
			@Nonnull
					AjaxRequestTarget target,
			@Nonnull
					PanelConstructor<T> constructor,
			@Nonnull
					T object) {
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
}
