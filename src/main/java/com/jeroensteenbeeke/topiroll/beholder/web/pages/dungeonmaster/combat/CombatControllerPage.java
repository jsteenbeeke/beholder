package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.combat;

import com.google.common.collect.ImmutableList;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableAdapter;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableBehavior;
import com.jeroensteenbeeke.hyperion.data.DomainObject;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.BootstrapBasePage;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.beans.MarkerService;
import com.jeroensteenbeeke.topiroll.beholder.dao.InitiativeParticipantDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.PinnedCompendiumEntryDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.TokenInstanceDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.InitiativeParticipantFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.PinnedCompendiumEntryFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.TokenInstanceFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.visitor.AreaMarkerVisitor;
import com.jeroensteenbeeke.topiroll.beholder.web.BeholderSession;
import com.jeroensteenbeeke.topiroll.beholder.web.components.*;
import com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.CompendiumWindow;
import com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.combat.InitiativePanel;
import com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.combat.MapOptionsPanel;
import com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.combat.MarkerStatusPanel;
import com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.combat.TokenStatusPanel;
import com.jeroensteenbeeke.topiroll.beholder.web.model.DependentModel;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.HomePage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.IdentityCoordinateTranslator;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.RunSessionPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.exploration.ExplorationControllerPage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.UrlUtils;
import org.apache.wicket.request.cycle.RequestCycle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CombatControllerPage extends BootstrapBasePage implements DMViewCallback {
	private static final String MODAL_ID = "modal";
	private static final String MARKER_ID = "marker";
	private static final String TOKEN_ID = "token";
	private static final String PARTICIPANT_ID = "participant";
	private static final long serialVersionUID = 8480811839688444273L;
	private final TokenStatusPanel tokenStatusPanel;
	private final MapOptionsPanel mapOptionsPanel;
	private final MarkerStatusPanel markerStatusPanel;
	private final WebMarkupContainer combatNavigator;
	private final InitiativePanel initiativePanel;

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

	@Inject
	private PinnedCompendiumEntryDAO compendiumEntryDAO;

	@Inject
	private TokenInstanceDAO tokenInstanceDAO;

	private final WebMarkupContainer preview;

	private final ICoordinateTranslator coordinateTranslator;

	private boolean disableClickListener = false;

	public CombatControllerPage(MapView view) {
		super("Combat Mode");

		if (BeholderSession.get().getUser() == null) {
			BeholderSession.get().invalidate();
			throw new RestartResponseAtInterceptPageException(HomePage.class);
		}

		viewModel = ModelMaker.wrap(view);
		viewModel.detach();

		Optional<ScaledMap> map = Optional.ofNullable(viewModel.getObject().getSelectedMap());

		final double displayFactor = map.map(m -> m.getDisplayFactor(view)).orElse(1.0);
		int desiredWidth = map.map(ScaledMap::getBasicWidth).map(w -> (int) displayFactor * w).orElse(0);

		if (map.isPresent()) {
			preview = new AbstractMapPreview("preview", map.get(), desiredWidth) {
				private static final long serialVersionUID = 8613385670220290868L;

				@Override
				protected void addOnDomReadyJavaScript(String canvasId, StringBuilder js,
													   double factor) {

				}

			};
			coordinateTranslator = (ICoordinateTranslator) preview;
		} else {
			preview = new WebMarkupContainer("preview");
			coordinateTranslator = new IdentityCoordinateTranslator();
		}

		preview.add(initiativePanel = new InitiativePanel("initiative", view, this));
		tokenStatusPanel = new TokenStatusPanel("tokenStatus", this);
		mapOptionsPanel = new MapOptionsPanel("mapOptions", view, this);
		markerStatusPanel = new MarkerStatusPanel("markerStatus", this);
		preview.add(tokenStatusPanel.setVisible(false));
		preview.add(mapOptionsPanel.setVisible(false));
		preview.add(markerStatusPanel.setVisible(false));

		preview.add(new OnClickBehavior() {
			private static final long serialVersionUID = 9004992789363069534L;

			@Override
			protected void onClick(AjaxRequestTarget target, ClickEvent event) {
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

		mapModel = map.<IModel<ScaledMap>> map(ModelMaker::wrap).orElseGet(Model::of);

		IModel<List<TokenInstance>> tokenModel = new LoadableDetachableModel<List<TokenInstance>>() {
			private static final long serialVersionUID = -8597299178957964301L;

			@Override
			protected List<TokenInstance> load() {
				return getCurrentMap().map(m -> {
					TokenInstanceFilter filter = new TokenInstanceFilter();
					filter.map(m);
					filter.currentHitpoints().greaterThanOrNull(0);
					filter.id().orderBy(true);

					return tokenInstanceDAO.findByFilter(filter).toJavaList();


				}).orElseGet(ImmutableList::of);
			}
		};

		preview.add(new ListView<TokenInstance>("tokens", tokenModel) {
			private static final long serialVersionUID = -8008864217633653948L;

			@Override
			protected void populateItem(ListItem<TokenInstance> item) {
				TokenInstance instance = item.getModelObject();

				int squareSize = getCurrentMap().map(ScaledMap::getSquareSize).orElse(0);

				int wh = squareSize
						* instance.getDefinition().getDiameterInSquares();

				Label image = new Label(TOKEN_ID, new DependentModel<TokenInstance, String>(item.getModel()) {
					private static final long serialVersionUID = 7184052381548459602L;

					@Override
					protected String load(TokenInstance object) {
						return object.getLabel();
					}
				});
				image.add(AttributeModifier.replace("style",
						new DependentModel<TokenInstance, String>(item.getModel()) {
							private static final long serialVersionUID = 1L;

							@Override
							protected String load(TokenInstance i) {
								int left = i.getOffsetX();
								int top = i.getOffsetY();

								left = coordinateTranslator.translateToScaledImageSize(left);
								top = coordinateTranslator.translateToScaledImageSize(top);

								int actualWH = coordinateTranslator.translateToScaledImageSize(wh);

								return String.format(
										"position: absolute; left: %1$dpx; top: %2$dpx; max-width: " +
												"%3$dpx !important; " +
												"width: %3$dpx; height: %3$dpx; max-height: %3$dpx " +
												"!important; background-size: %3$dpx %3$dpx; " +
												"border-radius: 100%%; border: 3px " +
												"%6$s #%4$s; background-image: url('%5$s'); " +
												"display: table-cell; vertical-align: bottom; " +
												"color: #cccccc; text-align: center; margin: 0; padding: 0;",
										left, top, actualWH, i
												.getBorderType().toHexColor(),
										i.getDefinition().getImageUrl(),
										i.isShow() ? "solid" : "dashed"
								);
							}

						}));
				image.add(AttributeModifier.replace("title", new DependentModel<TokenInstance, String>(item.getModel()) {
					private static final long serialVersionUID = 5933238244997270769L;

					@Override
					protected String load(TokenInstance instance) {
						return Optional.ofNullable(instance).filter(i -> i.getCurrentHitpoints() != null && i.getMaxHitpoints() != null).map(
								i -> 100 * i.getCurrentHitpoints() / i.getMaxHitpoints()
						).map(p -> String.format("%s (%d%% health)", instance.getBadge(), p)).orElse(instance.getBadge());
					}
				}));

				Options draggableOptions = new Options();
				draggableOptions.set("opacity", "0.5");
				draggableOptions.set("containment", Options.asString("parent"));
				image.add(new DependentStopEnabledDraggableBehavior<TokenInstance>(item.getModel(), draggableOptions) {
					private static final long serialVersionUID = -8573977590378308749L;

					@Override
					protected void onStop(AjaxRequestTarget target, TokenInstance instance, int left, int top) {
						left = coordinateTranslator.translateToRealImageSize(left);
						top = coordinateTranslator.translateToRealImageSize(top);

						mapService.updateTokenLocation(instance, left, top);

						redrawMap(target);
					}
				});

				image.add(new DependentOnClickBehavior<TokenInstance>(item.getModel()) {
					private static final long serialVersionUID = -6351045505325057830L;

					@Override
					protected void onClick(AjaxRequestTarget target, OnClickBehavior.ClickEvent event, TokenInstance instance) {
						selectedToken = ModelMaker.wrap(instance);
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
		}.setReuseItems(true));

		IModel<List<AreaMarker>> markerModel = new LoadableDetachableModel<List<AreaMarker>>() {
			private static final long serialVersionUID = 4934755224881469803L;

			@Override
			protected List<AreaMarker> load() {
				return viewModel.getObject().getMarkers().stream().sorted(Comparator.comparing(AreaMarker::getId)).collect(Collectors.toList());
			}
		};


		preview.add(new ListView<AreaMarker>("markers", markerModel) {
			private static final long serialVersionUID = 6225252705125595548L;
			@Inject
			private MarkerService markerService;

			@Override
			protected void populateItem(ListItem<AreaMarker> item) {
				AreaMarker areaMarker = item.getModelObject();

				int squareSize = getCurrentMap().map(ScaledMap::getSquareSize).orElse(0);

				int wh = squareSize * areaMarker.getExtent() / 5;

				MarkerStyleModel<?> markerStyleModel = areaMarker
						.visit(new AreaMarkerVisitor<MarkerStyleModel<?>>() {
							private static final long serialVersionUID = -7713349603953871476L;

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


								return new MarkerStyleModel<>(marker, displayFactor)
										.setX((m, factor) -> Math.round((m.getOffsetX() + wh) * factor))
										.setY((m, factor) -> Math.round((m.getOffsetY() + wh) * factor))
										.setWidth((m, factor) -> 0L)
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


								return new MarkerStyleModel<>(marker, displayFactor)
										.setX((m, factor) -> Math.round((m.getOffsetX() + wh) * factor))
										.setY((m, factor) -> Math.round((m.getOffsetY() + wh) * factor)).setWidth((m, factor) -> 0L)
										.setHeight((m, factor) -> 0L).setBorderTop((m, factor) -> "5px solid transparent").setBorderRight((m, factor) -> String.format("%fpx solid #%s",
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
									private static final long serialVersionUID = -8666575204421319185L;

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
												.update(marker, marker.getColor(), newX - wh,
														newY - wh,
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
												.update(marker, marker.getColor(), newX - wh,
														newY - wh,
														marker.getExtent(), marker
																.getTheta());

										return null;
									}
								});


							}
						}));
				marker.add(new OnClickBehavior() {
					private static final long serialVersionUID = 6159702123104745379L;

					@Override
					protected void onClick(AjaxRequestTarget target, ClickEvent event) {
						selectedToken = Model.of();
						clickedLocation = null;
						selectedMarker = ModelMaker.wrap(item.getModelObject());

						mapOptionsPanel.setVisible(false);
						tokenStatusPanel.setVisible(false);
						markerStatusPanel.setVisible(true);
						target.add(tokenStatusPanel, mapOptionsPanel, markerStatusPanel);
					}
				}.withoutPropagation());

				item.add(marker);
			}
		}.setReuseItems(true));

		IModel<List<InitiativeParticipant>> participantModel = new LoadableDetachableModel<List<InitiativeParticipant>>() {
			private static final long serialVersionUID = -5936466884520702271L;

			@Override
			protected List<InitiativeParticipant> load() {
				InitiativeParticipantFilter initFilter = new InitiativeParticipantFilter();
				initFilter.player(true);
				initFilter.view(viewModel.getObject());
				initFilter.id().orderBy(true);
				return participantDAO.findByFilter(initFilter).toJavaList();
			}
		};

		preview.add(new ListView<InitiativeParticipant>("participants",
				participantModel) {

			private static final long serialVersionUID = 7380087785300362119L;

			@Override
			protected void populateItem(ListItem<InitiativeParticipant> item) {
				InitiativeParticipant participant = item.getModelObject();
				int wh = getCurrentMap().map(ScaledMap::getSquareSize).orElse(0);

				Label image = new Label(PARTICIPANT_ID, participant.getName());
				image.add(AttributeModifier.replace("style",
						new LoadableDetachableModel<String>() {
							private static final long serialVersionUID = 1L;

							@Override
							protected String load() {
								InitiativeParticipant participant = item.getModelObject();

								int left = Optional.ofNullable(participant.getOffsetX())
										.map(Math::abs)
										.orElseGet(() -> getCurrentMap().map(ScaledMap::getBasicWidth).map(w -> (int) (w * displayFactor / 2)).orElse(0));
								int top = Optional.ofNullable(participant.getOffsetY())
										.map(Math::abs)
										.orElseGet(() -> getCurrentMap().map(ScaledMap::getBasicHeight).map(w -> (int) (w * displayFactor / 2)).orElse(0));

								left = coordinateTranslator.translateToScaledImageSize(left);
								top = coordinateTranslator.translateToScaledImageSize(top);

								int actualWH = coordinateTranslator.translateToScaledImageSize(wh);


								return String.format(
										"position: absolute; left: %1$dpx; top: %2$dpx; max-width: %3$dpx !important;" +
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
					private static final long serialVersionUID = -3342358052152683138L;

					@Override
					protected void onClick(AjaxRequestTarget target,
										   ClickEvent event) {
						// TODO: What happens when clicking a player marker
					}
				});

				item.add(image);
			}
		}.setReuseItems(true));

		combatNavigator = new WebMarkupContainer("combatNavigator");
		combatNavigator.setOutputMarkupId(true);

		combatNavigator.add(new Link<MapView>("back", ModelMaker.wrap(view)) {
			private static final long serialVersionUID = -575729727460058566L;

			@Override
			public void onClick() {
				setResponsePage(new RunSessionPage());
			}
		});

		combatNavigator.add(new Link<MapView>("exploration", ModelMaker.wrap(view)) {
			private static final long serialVersionUID = 4519537256150506040L;

			@Override
			public void onClick() {
				setResponsePage(new ExplorationControllerPage(getModelObject()));
			}
		});

		combatNavigator.add(new AjaxLink<MapView>("compendium", ModelMaker.wrap(view)) {
			private static final long serialVersionUID = -5342617572033020429L;

			@Override
			public void onClick(AjaxRequestTarget target) {

				createModalWindow(target, CompendiumWindow::new, null);
			}

		});

		PinnedCompendiumEntryFilter filter = new PinnedCompendiumEntryFilter();
		filter.pinnedBy().set(BeholderSession.get().getUser());

		IModel<List<CompendiumEntry>> pinnedEntryModel = new LoadableDetachableModel<List<CompendiumEntry>>() {
			private static final long serialVersionUID = 827576365560927640L;

			@Override
			protected List<CompendiumEntry> load() {
				return compendiumEntryDAO.findByFilter(filter)
						.map(PinnedCompendiumEntry::getEntry)
						.sorted(Comparator.comparing(CompendiumEntry::getTitle))
						.toJavaList();
			}
		};


		combatNavigator.add(new ListView<CompendiumEntry>("pinnedEntries", pinnedEntryModel) {

			private static final long serialVersionUID = 5963453746408369930L;

			@Override
			protected void populateItem(ListItem<CompendiumEntry> item) {
				AjaxLink<CompendiumEntry> entryLink = new AjaxLink<CompendiumEntry>("entry", item.getModel()) {
					private static final long serialVersionUID = 1531487112400990426L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						createModalWindow(target, CompendiumWindow::new, getModelObject());

					}
				};
				entryLink.add(new Label("label", Model.of("Compendium: "+ item.getModelObject().getTitle())));
				item.add(entryLink);

			}
		});
		preview.add(combatNavigator);


		add(preview);

		add(modal = new WebMarkupContainer(MODAL_ID));
		modal.setOutputMarkupPlaceholderTag(true);
	}

	private Optional<ScaledMap> getCurrentMap() {
		return Optional.ofNullable(mapModel.getObject());
	}

	@Override
	public void redrawMap(AjaxRequestTarget target) {
		clickedLocation = null;
		previousClickedLocation = null;
		if (preview instanceof AbstractMapPreview) {
			((AbstractMapPreview) preview).refresh(target);
		}
	}

	@Override
	public void refreshMenus(AjaxRequestTarget target) {
		target.add(combatNavigator, tokenStatusPanel, mapOptionsPanel, markerStatusPanel, initiativePanel);
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
			@Nullable
					T object) {
		disableClickListener = true;
		Component oldModal = modal;
		oldModal.replaceWith(modal = constructor.apply(MODAL_ID, object, this));
		target.add(modal);
		target.appendJavaScript("$('#combat-modal').modal('show');");
	}

	@Override
	public <T extends DomainObject> void createModalWindow(@Nonnull AjaxRequestTarget target, @Nonnull WindowConstructor<T> constructor, @Nullable T object) {
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
