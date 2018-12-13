package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.exploration;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableAdapter;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableBehavior;
import com.jeroensteenbeeke.hyperion.data.DomainObject;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.BootstrapBasePage;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarShapeDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.InitiativeParticipantDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.PinnedCompendiumEntryDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.FogOfWarShapeFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.InitiativeParticipantFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.PinnedCompendiumEntryFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.BeholderSession;
import com.jeroensteenbeeke.topiroll.beholder.web.components.*;
import com.jeroensteenbeeke.topiroll.beholder.web.components.exploration.CompendiumPanel;
import com.jeroensteenbeeke.topiroll.beholder.web.components.exploration.ExplorationModeCallback;
import com.jeroensteenbeeke.topiroll.beholder.web.components.exploration.HideRevealPanel;
import com.jeroensteenbeeke.topiroll.beholder.web.components.exploration.TokenStatusPanel;
import com.jeroensteenbeeke.topiroll.beholder.web.model.DependentModel;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.HomePage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.ControlViewPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.combat.CombatControllerPage;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExplorationControllerPage extends BootstrapBasePage implements ExplorationModeCallback {
	private static final String MODAL_ID = "modal";
	private static final String TOKEN_ID = "token";
	private static final String PARTICIPANT_ID = "participant";
	private final WebMarkupContainer explorationNavigator;
	private final HideRevealPanel hideReveal;
	private final TokenStatusPanel tokenStatusPanel;

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
	private FogOfWarShapeDAO shapeDAO;

	private final AbstractMapPreview preview;

	private boolean disableClickListener = false;

	public ExplorationControllerPage(MapView view) {
		super("Exploration Mode");

		if (BeholderSession.get().getUser() == null) {
			BeholderSession.get().invalidate();
			throw new RestartResponseAtInterceptPageException(HomePage.class);
		}

		viewModel = ModelMaker.wrap(view);

		ScaledMap map = view.getSelectedMap();

		if (map == null) {
			throw new RestartResponseAtInterceptPageException(new ControlViewPage(view));
		}

		final double displayFactor = map.getDisplayFactor(view);
		int desiredWidth = (int) (displayFactor * map.getBasicWidth());

		preview = new AbstractMapPreview("preview", map, desiredWidth) {
			@Override
			protected void addOnDomReadyJavaScript(String canvasId, StringBuilder js,
												   double factor) {
				redrawShapes(canvasId, js);

			}

			private void redrawShapes(String canvasId, StringBuilder js) {
				FogOfWarShapeFilter shapeFilter = new FogOfWarShapeFilter();
				shapeFilter.map(map);

				shapeDAO.findByFilter(shapeFilter).forEach(shape -> js.append(shape.visit(new ExplorationShapeRenderer(canvasId, displayFactor, viewModel.getObject()))));


			}

			@Override
			public void refresh(AjaxRequestTarget target) {
				super.refresh(target);

				String canvasId = canvas.getMarkupId();
				String canvas = "document.multi" + canvasId;

				StringBuilder js = new StringBuilder();
				js.append(String.format("%s.clearAll();\n", canvas));
				renderMap(js);
				redrawShapes(canvas, js);
				target.appendJavaScript(js);
			}
		};

		preview.add(hideReveal = new HideRevealPanel("reveal", view, this));
		hideReveal.setVisible(false);

		tokenStatusPanel = new TokenStatusPanel("tokenStatus", this);
		preview.add(tokenStatusPanel.setVisible(false));

		preview.add(new OnClickBehavior() {
			@Override
			protected void onClick(AjaxRequestTarget target, ClickEvent event) {
				if (!disableClickListener) {
					previousClickedLocation = clickedLocation;
					clickedLocation = new Point((int) (event.getOffsetLeft() / displayFactor), (int)

							(event
									.getOffsetTop() / displayFactor));
					selectedMarker = Model.of();
					selectedToken = Model.of();

					hideReveal.setVisible(true);
					tokenStatusPanel.setVisible(false);

					target.add(hideReveal, tokenStatusPanel);
				}
			}
		});

		mapModel = ModelMaker.wrap(map);

		IModel<List<TokenInstance>> tokenModel = new LoadableDetachableModel<List<TokenInstance>>() {
			@Override
			protected List<TokenInstance> load() {
				return mapModel.getObject().getTokens().stream()
						.filter(t -> t.getCurrentHitpoints() == null || t.getCurrentHitpoints() > 0)
						.sorted(Comparator.comparing(TokenInstance::getId))
						.collect(Collectors.toList());
			}
		};

		preview.add(new ListView<TokenInstance>("tokens", tokenModel) {
			@Override
			protected void populateItem(ListItem<TokenInstance> item) {
				TokenInstance instance = item.getModelObject();

				int squareSize = map.getSquareSize();

				int wh = squareSize
						* instance.getDefinition().getDiameterInSquares();

				Label image = new Label(TOKEN_ID, new DependentModel<TokenInstance, String>(item.getModel()) {
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

								left = preview.translateToScaledImageSize(left);
								top = preview.translateToScaledImageSize(top);

								int actualWH = preview.translateToScaledImageSize(wh);

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
					@Override
					protected void onStop(AjaxRequestTarget target, TokenInstance instance, int left, int top) {
						mapService.updateTokenLocation(
								instance, (int) (left / displayFactor), (int) (top /
										displayFactor));

						redrawMap(target);
					}
				});

				image.add(new DependentOnClickBehavior<TokenInstance>(item.getModel()) {
					@Override
					protected void onClick(AjaxRequestTarget target, ClickEvent event, TokenInstance instance) {
						selectedToken = ModelMaker.wrap(instance);
						clickedLocation = null;
						selectedMarker = Model.of();

						hideReveal.setVisible(false);
						tokenStatusPanel.setVisible(true);

						refreshMenus(target);
					}
				}.withoutPropagation());

				item.add(image);

			}
		}.setReuseItems(true));

		IModel<List<AreaMarker>> markerModel = new LoadableDetachableModel<List<AreaMarker>>() {
			@Override
			protected List<AreaMarker> load() {
				return viewModel.getObject().getMarkers().stream().sorted(Comparator.comparing(AreaMarker::getId)).collect(Collectors.toList());
			}
		};


		IModel<List<InitiativeParticipant>> participantModel = new LoadableDetachableModel<List<InitiativeParticipant>>() {
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
					@Override
					protected void onClick(AjaxRequestTarget target,
										   ClickEvent event) {
						// TODO: What happens when clicking a player marker
					}
				});

				item.add(image);
			}
		}.setReuseItems(true));

		explorationNavigator = new WebMarkupContainer("explorationNavigator");
		explorationNavigator.setOutputMarkupId(true);

		explorationNavigator.add(new Link<MapView>("back", ModelMaker.wrap(view)) {
			@Override
			public void onClick() {
				setResponsePage(new ControlViewPage(getModelObject()));
			}
		});

		explorationNavigator.add(new Link<MapView>("combat", ModelMaker.wrap(view)) {
			@Override
			public void onClick() {
				setResponsePage(new CombatControllerPage(getModelObject()));
			}
		});

		explorationNavigator.add(new AjaxLink<MapView>("compendium", ModelMaker.wrap(view)) {
			@Override
			public void onClick(AjaxRequestTarget target) {

				createModalWindow(target, CompendiumPanel::new, null);
			}

		});

		PinnedCompendiumEntryFilter filter = new PinnedCompendiumEntryFilter();
		filter.pinnedBy().set(BeholderSession.get().getUser());

		IModel<List<CompendiumEntry>> pinnedEntryModel = new LoadableDetachableModel<List<CompendiumEntry>>() {
			@Override
			protected List<CompendiumEntry> load() {
				return compendiumEntryDAO.findByFilter(filter)
						.map(PinnedCompendiumEntry::getEntry)
						.sorted(Comparator.comparing(CompendiumEntry::getTitle))
						.toJavaList();
			}
		};


		explorationNavigator.add(new ListView<CompendiumEntry>("pinnedEntries", pinnedEntryModel) {

			@Override
			protected void populateItem(ListItem<CompendiumEntry> item) {
				AjaxLink<CompendiumEntry> entryLink = new AjaxLink<CompendiumEntry>("entry", item.getModel()) {
					@Override
					public void onClick(AjaxRequestTarget target) {
						createModalWindow(target, CompendiumPanel::new, getModelObject());

					}
				};
				entryLink.add(new Label("label", Model.of("Compendium: " + item.getModelObject().getTitle())));
				item.add(entryLink);

			}
		});
		preview.add(explorationNavigator);


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
	public void refreshMenus(AjaxRequestTarget target) {
		target.add(explorationNavigator, tokenStatusPanel);
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
