package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.exploration;

import com.google.common.collect.ImmutableList;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableAdapter;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableBehavior;
import com.jeroensteenbeeke.hyperion.data.DomainObject;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.BootstrapBasePage;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarGroupVisibilityDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarShapeDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.InitiativeParticipantDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.PinnedCompendiumEntryDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.FogOfWarShapeFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.InitiativeParticipantFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.PinnedCompendiumEntryFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.visitor.FogOfWarShapeVisitor;
import com.jeroensteenbeeke.topiroll.beholder.web.BeholderSession;
import com.jeroensteenbeeke.topiroll.beholder.web.components.*;
import com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.CompendiumWindow;
import com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.exploration.*;
import com.jeroensteenbeeke.topiroll.beholder.web.data.visitors.FogOfWarShapeXCoordinateVisitor;
import com.jeroensteenbeeke.topiroll.beholder.web.data.visitors.FogOfWarShapeYCoordinateVisitor;
import com.jeroensteenbeeke.topiroll.beholder.web.model.DependentModel;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.HomePage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.IdentityCoordinateTranslator;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.RunSessionPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.combat.CombatControllerPage;
import io.vavr.control.Option;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
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

public class ExplorationControllerPage extends BootstrapBasePage implements DMViewCallback {
	private static final long serialVersionUID = 383172566857420866L;

	public static final String MODAL_ID = "modal";
	private static final String TOKEN_ID = "token";
	private static final String PARTICIPANT_ID = "participant";
	private final WebMarkupContainer explorationNavigator;
	private final HideRevealPanel hideReveal;
	private final TokenStatusPanel tokenStatusPanel;

	private Integer scrollToX;

	private Integer scrollToY;

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

	private final WebMarkupContainer preview;

	private final ICoordinateTranslator coordinateTranslator;

	private boolean disableClickListener = false;
	private double displayFactor;

	public ExplorationControllerPage(MapView view) {
		this(view, null);
	}

	public ExplorationControllerPage(MapView view, FogOfWarGroup focusGroup) {
		super("Exploration Mode");

		if (BeholderSession.get().getUser() == null) {
			BeholderSession.get().invalidate();
			throw new RestartResponseAtInterceptPageException(HomePage.class);
		}

		viewModel = view != null ? ModelMaker.wrap(view) : Model.of();

		ScaledMap map = view == null ? null : view.getSelectedMap();

		displayFactor = map == null ? 1.0 : map.getDisplayFactor(view);

		if (focusGroup != null) {
			scrollToX = focusGroup.getShapes().stream()
								  .map(s -> s.visit(new FogOfWarShapeXCoordinateVisitor()))
								  .min(Comparator.naturalOrder())
								  .map(i -> (int) (i * displayFactor))
								  .orElse(null);

			scrollToY = focusGroup.getShapes().stream()
								  .map(s -> s.visit(new FogOfWarShapeYCoordinateVisitor()))
								  .min(Comparator.naturalOrder())
								  .map(i -> (int) (i * displayFactor))
								  .orElse(null);


		}

		int desiredWidth = map != null ? (int) (displayFactor * map.getBasicWidth()) : 1080;

		if (map == null) {
			preview = new WebMarkupContainer("preview");
			coordinateTranslator = new IdentityCoordinateTranslator();
		} else {

			preview = new AbstractMapPreview("preview", map, desiredWidth) {
				private static final long serialVersionUID = -5400399261540169818L;

				@Inject
				private FogOfWarGroupVisibilityDAO visibilityDAO;

				@Override
				protected void addOnDomReadyJavaScript(String canvasId, StringBuilder js,
													   double factor) {
					redrawShapes(canvasId, js);

				}

				private void redrawShapes(String canvasId, StringBuilder js) {
					FogOfWarShapeFilter shapeFilter = new FogOfWarShapeFilter();
					shapeFilter.map(map);

					shapeDAO.findByFilter(shapeFilter).forEach(shapeDAO::evict);
					shapeDAO
						.findByFilter(shapeFilter)
						.forEach(shape -> js.append(shape.visit(new ExplorationShapeRenderer(canvasId, displayFactor, viewModel
							.getObject(), visibilityDAO))));
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
			coordinateTranslator = (AbstractMapPreview) preview;
		}

		preview.add(hideReveal = new HideRevealPanel("reveal", viewModel.getObject(), this));
		hideReveal.setVisible(false);

		tokenStatusPanel = new TokenStatusPanel("tokenStatus", this);
		preview.add(tokenStatusPanel.setVisible(false));

		preview.add(new OnClickBehavior() {
			private static final long serialVersionUID = -7064427584475892891L;

			@Override
			protected void onClick(AjaxRequestTarget target, ClickEvent event) {
				if (!disableClickListener) {
					ExplorationControllerPage.this.onClick(event);

					hideReveal.setVisible(true);
					tokenStatusPanel.setVisible(false);

					target.add(hideReveal, tokenStatusPanel);
				}
			}
		});

		mapModel = ModelMaker.wrap(map);

		IModel<List<TokenInstance>> tokenModel = new LoadableDetachableModel<List<TokenInstance>>() {
			private static final long serialVersionUID = -2933394249687702586L;

			@Override
			protected List<TokenInstance> load() {
				return Optional
					.ofNullable(mapModel.getObject())
					.map(map -> map.getTokens().stream()
								   .filter(t -> t.getCurrentHitpoints() == null || t.getCurrentHitpoints() > 0)
								   .sorted(Comparator.comparing(TokenInstance::getId))
								   .collect(Collectors.toList()))
					.orElseGet(ImmutableList::of);
			}
		};

		preview.add(new ListView<>("tokens", tokenModel) {
			private static final long serialVersionUID = 3407286207428917532L;

			@Override
			protected void populateItem(ListItem<TokenInstance> item) {
				TokenInstance instance = item.getModelObject();

				int squareSize = Optional.ofNullable(map).map(ScaledMap::getSquareSize).orElse(0);

				int wh = squareSize * instance.getDefinition().getDiameterInSquares();

				Label image = new Label(TOKEN_ID, new DependentModel<TokenInstance, String>(item.getModel()) {
					private static final long serialVersionUID = -9052475381415005280L;

					@Override
					protected String load(TokenInstance object) {
						return object.getLabel();
					}
				});
				image.add(AttributeModifier.replace("style", new DependentModel<TokenInstance, String>(item.getModel()) {
					private static final long serialVersionUID = 1L;

					@Override
					protected String load(TokenInstance i) {
						int left = i.getOffsetX();
						int top = i.getOffsetY();

						left = coordinateTranslator.translateToScaledImageSize(left);
						top = coordinateTranslator.translateToScaledImageSize(top);

						int actualWH = coordinateTranslator.translateToScaledImageSize(wh);

						return String.format(
							"position: absolute; left: %1$dpx; top: %2$dpx; max-width: "
								+ "%3$dpx !important; "
								+ "width: %3$dpx; height: %3$dpx; max-height: %3$dpx "
								+ "!important; background-size: %3$dpx %3$dpx; "
								+ "border-radius: 100%%; border: 3px "
								+ "%6$s #%4$s; background-image: url('%5$s'); "
								+ "display: table-cell; vertical-align: bottom; "
								+ "color: #cccccc; text-align: center; margin: 0; padding: 0;",
							left, top, actualWH, i.getBorderType().toHexColor(),
							i.getDefinition().getImageUrl(),
							i.isShow() ? "solid" : "dashed");
					}

				}));
				image.add(AttributeModifier.replace("title", new DependentModel<TokenInstance, String>(item.getModel()) {
					private static final long serialVersionUID = -1407024057458015547L;

					@Override
					protected String load(TokenInstance instance) {
						return Optional.ofNullable(instance).filter(
							i -> i.getCurrentHitpoints() != null
								&& i.getMaxHitpoints() != null).map(
								i -> 100 * i.getCurrentHitpoints() / i.getMaxHitpoints()).map(p -> String
							.format("%s (%d%% health)", instance.getBadge(), p)).orElse(instance.getBadge());
					}
				}));

				Options draggableOptions = new Options();
				draggableOptions.set("opacity", "0.5");
				draggableOptions.set("containment", Options.asString("parent"));
				image.add(new DependentStopEnabledDraggableBehavior<>(item.getModel(),
					draggableOptions) {
					private static final long serialVersionUID = -1407439608333482730L;

					@Override
					protected void onStop(AjaxRequestTarget target,
						TokenInstance instance, int left, int top) {
						left = coordinateTranslator.translateToRealImageSize(left);
						top = coordinateTranslator.translateToRealImageSize(top);

						mapService.updateTokenLocation(instance, left, top);

						redrawMap(target);
					}
				});

				image.add(new DependentOnClickBehavior<>(item.getModel()) {
					private static final long serialVersionUID = -5156716010790702323L;

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


		IModel<List<InitiativeParticipant>> participantModel = new LoadableDetachableModel<List<InitiativeParticipant>>() {
			private static final long serialVersionUID = -8093729309766140058L;

			@Override
			protected List<InitiativeParticipant> load() {
				InitiativeParticipantFilter initFilter = new InitiativeParticipantFilter();
				initFilter.player(true);
				initFilter.view(viewModel.getObject());
				initFilter.id().orderBy(true);
				return participantDAO.findByFilter(initFilter).toJavaList();
			}
		};

		preview.add(new ListView<>("participants", participantModel) {

			private static final long serialVersionUID = -1880525125397252346L;

			@Override
			protected void populateItem(ListItem<InitiativeParticipant> item) {
				InitiativeParticipant participant = item.getModelObject();
				int wh = Optional.ofNullable(map).map(ScaledMap::getSquareSize).orElse(0);

				Label image = new Label(PARTICIPANT_ID, participant.getName());
				image.add(AttributeModifier.replace("style", new LoadableDetachableModel<String>() {
					private static final long serialVersionUID = 1L;

					@Override
					protected String load() {
						InitiativeParticipant participant = item.getModelObject();

						int left = Option.of(participant.getOffsetX()).map(Math::abs).orElse(() -> Option.of(map)
							.map(ScaledMap::getBasicWidth).map(
								width -> (int) (width * displayFactor / 2))).getOrElse(0);
						int top = Option.of(participant.getOffsetY()).map(Math::abs).orElse(() -> Option.of(map)
							.map(ScaledMap::getBasicHeight).map(
								width -> (int) (width * displayFactor / 2))).getOrElse(0);

						left = coordinateTranslator.translateToScaledImageSize(left);
						top = coordinateTranslator.translateToScaledImageSize(top);

						int actualWH = coordinateTranslator.translateToScaledImageSize(wh);

						return String.format(
							"position: absolute; left: %1$dpx; top: %2$dpx; max-width: %3$dpx !important;"
								+ " "
								+ "width: %3$dpx; height: %3$dpx; max-height: %3$dpx "
								+ "!important; border-radius: 100%%; border: 1px "
								+ "solid" + " "
								+ "#00ff00; text-align: center; word-break: "
								+ "break-all; vertical-align: middle; display: "
								+ "table-cell; color: #cccccc; "
								+ "background-image: url('%4$s'); background-size: "
								+ "%3$dpx %3$dpx;", left, top, actualWH,
							UrlUtils.rewriteToContextRelative("img/player.png",
								RequestCycle.get()));
					}

				}));
				image.add(AttributeModifier.replace("title", participant.getName()));

				Options draggableOptions = new Options();
				draggableOptions.set("opacity", "0.5");
				draggableOptions.set("containment", Options.asString("parent"));
				image.add(new

					DraggableBehavior(draggableOptions, new DraggableAdapter() {
					private static final long serialVersionUID = 1L;

					@Override
					public boolean isStopEventEnabled() {

						return true;
					}

					@Override
					public void onDragStop(AjaxRequestTarget target, int top,
						int left) {
						super.onDragStop(target, top, left);

						// TODO: Service method?
						InitiativeParticipant participant = item.getModelObject();
						participant.setOffsetX((int) (left / displayFactor));
						participant.setOffsetY((int) (top / displayFactor));
						participantDAO.update(participant);

					}
				})).add(new OnClickBehavior() {
					private static final long serialVersionUID = 5502865667110141675L;

					@Override
					protected void onClick(AjaxRequestTarget target, ClickEvent event) {
						// TODO: What happens when clicking a player marker
					}
				});

				item.add(image);
			}
		}.setReuseItems(true));

		explorationNavigator = new WebMarkupContainer("explorationNavigator");
		explorationNavigator.setOutputMarkupId(true);

		explorationNavigator.add(new Link<MapView>("back") {
			private static final long serialVersionUID = 139235321283531063L;

			@Override
			public void onClick() {
				setResponsePage(new RunSessionPage());
			}
		});

		explorationNavigator.add(new Link<MapView>("combat") {
			private static final long serialVersionUID = -5688463903385508478L;

			@Override
			public void onClick() {
				setResponsePage(new CombatControllerPage(viewModel.getObject()));
			}
		});
		explorationNavigator.add(new AjaxLink<MapView>("playlists") {
			private static final long serialVersionUID = 3284435177217199400L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				createModalWindow(target, YoutubePlaylistWindow::new, viewModel.getObject());
			}
		});

		explorationNavigator.add(new AjaxLink<MapView>("compendium") {
			private static final long serialVersionUID = 8427376168422027789L;

			@Override
			public void onClick(AjaxRequestTarget target) {

				createModalWindow(target, CompendiumWindow::new, null);
			}

		});

		PinnedCompendiumEntryFilter filter = new PinnedCompendiumEntryFilter();
		filter.pinnedBy().set(BeholderSession.get().getUser());

		IModel<List<CompendiumEntry>> pinnedEntryModel = new LoadableDetachableModel<List<CompendiumEntry>>() {
			private static final long serialVersionUID = 321158461926325220L;

			@Override
			protected List<CompendiumEntry> load() {
				return compendiumEntryDAO.findByFilter(filter)
										 .map(PinnedCompendiumEntry::getEntry)
										 .sorted(Comparator.comparing(CompendiumEntry::getTitle))
										 .toJavaList();
			}
		};


		explorationNavigator.add(
			new ListView<>("pinnedEntries", pinnedEntryModel) {

				private static final long serialVersionUID = -3883519466730219132L;

				@Override
				protected void populateItem(ListItem<CompendiumEntry> item) {
					AjaxLink<CompendiumEntry> entryLink = new AjaxLink<CompendiumEntry>(
						"entry", item.getModel()) {
						private static final long serialVersionUID = -2462222055329944007L;

						@Override
						public void onClick(AjaxRequestTarget target) {
							createModalWindow(target, CompendiumWindow::new,
								getModelObject());

						}
					};
					entryLink.add(new Label("label", Model
						.of("Compendium: " + item.getModelObject().getTitle())));
					item.add(entryLink);

				}
			});

		explorationNavigator.add(new AjaxLink<MapView>("portraits") {
			private static final long serialVersionUID = 4682088219303952250L;

			@Override
			public void onClick(AjaxRequestTarget target) {

				createModalWindow(target, PortraitsWindow::new, viewModel.getObject());
			}

		});

		explorationNavigator.add(new AjaxLink<MapView>("mapselect") {
			private static final long serialVersionUID = 8427376168422027789L;

			@Override
			public void onClick(AjaxRequestTarget target) {

				createModalWindow(target, MapSelectWindow::new, viewModel.getObject());
			}

		});


		preview.add(explorationNavigator);


		add(preview);

		add(modal = new WebMarkupContainer(MODAL_ID));
		modal.setOutputMarkupPlaceholderTag(true);
	}

	public void onClick(OnClickBehavior.ClickEvent event) {
		previousClickedLocation = clickedLocation;
		clickedLocation = new Point((int) (event.getOffsetLeft() / displayFactor), (int)

			(event
				.getOffsetTop() / displayFactor));
		selectedMarker = Model.of();
		selectedToken = Model.of();
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
	public Optional<Point> getClickedLocation() {
		return Optional.ofNullable(clickedLocation);
	}

	@Override
	public Optional<Point> getPreviousClickedLocation() {
		return Optional.ofNullable(previousClickedLocation);
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
		try {
			oldModal.replaceWith(modal = constructor.apply(MODAL_ID, object, this));
			target.add(modal);
			target.appendJavaScript("$('#combat-modal').modal('show');");
		} catch (DMModalWindow.CannotCreateModalWindowException e) {
			// Silent ignore. This exception is a way to abort creating the window when encountering
			// inconsistent state
		}
	}

	@Override
	public <T extends DomainObject> void createModalWindow(@Nonnull AjaxRequestTarget target, @Nonnull WindowConstructor<T> constructor, @Nullable T object) {
		disableClickListener = true;
		Component oldModal = modal;
		try {
			oldModal.replaceWith(modal = constructor.apply(MODAL_ID, object, this));
			target.add(modal);
			target.appendJavaScript("$('#combat-modal').modal('show');");
		} catch (DMModalWindow.CannotCreateModalWindowException e) {
			// Silent ignore. This exception is a way to abort creating the window when encountering
			// inconsistent state
		}
	}

	@Override
	public void removeModal(AjaxRequestTarget target) {
		Component oldModal = modal;
		oldModal.replaceWith(modal = new WebMarkupContainer(MODAL_ID)
			.setOutputMarkupPlaceholderTag(true)
			.setVisible(false));
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

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);

		if (scrollToX != null && scrollToY != null) {
			StringBuilder script = new StringBuilder();
			script.append("var w = Math.max(document.documentElement.clientWidth, window.innerWidth || 0);\n");
			script.append("var h = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);\n");
			script.append(String.format("window.scrollTo(%d - (w / 4), %d - (h / 4));", scrollToX,
										scrollToY));

			response.render(
				OnDomReadyHeaderItem.forScript(script));
		}
	}
}
