/*
 * This file is part of Beholder
 * Copyright (C) 2016 - 2023 Jeroen Steenbeeke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.play.exploration;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableAdapter;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableBehavior;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.DungeonMasterNoteFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.FogOfWarShapeFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.InitiativeParticipantFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.PinnedCompendiumEntryFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.BeholderSession;
import com.jeroensteenbeeke.topiroll.beholder.web.components.*;
import com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.AddToSessionLogWindow;
import com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.CompendiumWindow;
import com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.exploration.*;
import com.jeroensteenbeeke.topiroll.beholder.web.data.visitors.FogOfWarShapeXCoordinateVisitor;
import com.jeroensteenbeeke.topiroll.beholder.web.data.visitors.FogOfWarShapeYCoordinateVisitor;
import com.jeroensteenbeeke.topiroll.beholder.web.model.DependentModel;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.HomePage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.IdentityCoordinateTranslator;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.RunSessionPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.play.StatefulMapControllerPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.play.combat.CombatControllerPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.play.state.*;
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
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.UrlUtils;
import org.apache.wicket.request.cycle.RequestCycle;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javax.inject.Inject;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExplorationControllerPage extends StatefulMapControllerPage implements DMViewCallback {
	private static final long serialVersionUID = 383172566857420866L;

	private static final String TOKEN_ID = "token";
	private static final String PARTICIPANT_ID = "participant";
	private final WebMarkupContainer explorationNavigator;
	private final TokenStatusPanel tokenStatusPanel;
	private final HideRevealPanel hideReveal;

	private Integer scrollToX;

	private Integer scrollToY;

	private final IModel<ScaledMap> mapModel;

	private final IModel<MapView> viewModel;

	@Inject
	private MapService mapService;

	@Inject
	private InitiativeParticipantDAO participantDAO;

	@Inject
	private PinnedCompendiumEntryDAO compendiumEntryDAO;

	@Inject
	private FogOfWarShapeDAO shapeDAO;

	@Inject
	private DungeonMasterNoteDAO noteDAO;

	private final WebMarkupContainer preview;

	private final ICoordinateTranslator coordinateTranslator;

	private double displayFactor;

	public ExplorationControllerPage(@NotNull MapView view) {
		this(view, null);
	}

	public ExplorationControllerPage(@NotNull MapView view, FogOfWarGroup focusGroup) {
		super("Exploration Mode");

		if (BeholderSession.get().getUser() == null) {
			BeholderSession.get().invalidate();
			throw new RestartResponseAtInterceptPageException(HomePage.class);
		}

		viewModel = ModelMaker.wrap(view);

		ScaledMap map = view.getSelectedMap();

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

		preview.add(hideReveal = new HideRevealPanel("reveal", viewModel.getObject(), this) {
			private static final long serialVersionUID = 393929764983365916L;

			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(visit(new BooleanMapViewStateVisitor() {
					@Override
					public Boolean visit(LocationClickedState locationClickedState) {
						return true;
					}
				}));
			}
		});

		tokenStatusPanel = new TokenStatusPanel("tokenStatus", this) {
			private static final long serialVersionUID = 685734562214333149L;

			@Override
			protected void onConfigure() {
				super.onConfigure();

				setVisible(visit(new BooleanMapViewStateVisitor() {
					@Override
					public Boolean visit(TokenInstanceClickedState clickedState) {
						return true;
					}
				}));
			}
		};
		preview.add(tokenStatusPanel.setVisible(false));

		preview.add(new OnClickBehavior() {
			private static final long serialVersionUID = -7064427584475892891L;

			@Override
			protected void onClick(AjaxRequestTarget target, ClickEvent event) {
				if (!disableClickListener) {
					ExplorationControllerPage.this.onClick(event, target);
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
					.orElseGet(List::of);
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

						String urlFormat = "url('%1$s')";
						String imageUrl = String.format(urlFormat, i.getDefinition().getImageUrl());
						if (i.getStatusEffect() != null) {
							String statusImageUrl = String.format(urlFormat,
																  UrlUtils.rewriteToContextRelative("img/statuseffects/" + i
																	  .getStatusEffect() + ".png", RequestCycle.get()));
							imageUrl = String.join(", ", statusImageUrl, imageUrl);
						}

						return String.format(
							"position: absolute; left: %1$dpx; top: %2$dpx; max-width: "
								+ "%3$dpx !important; "
								+ "width: %3$dpx; height: %3$dpx; max-height: %3$dpx "
								+ "!important; background-size: %3$dpx %3$dpx; "
								+ "border-radius: 100%%; border: 3px "
								+ "%6$s #%4$s; background-image: %5$s; "
								+ "display: table-cell; vertical-align: bottom; "
								+ "color: #cccccc; text-align: center; margin: 0; padding: 0;",
							left, top, actualWH, i.getBorderType().toHexColor(),
							imageUrl,
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
						onTokenClicked(instance);
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

						int left = Option.of(participant.getOffsetX()).map(Math::abs).orElse(() -> Option
							.of(map)
							.map(ScaledMap::getBasicWidth)
							.map(
								width -> (int) (width * displayFactor / 2))).getOrElse(0);
						int top = Option.of(participant.getOffsetY()).map(Math::abs).orElse(() -> Option
							.of(map)
							.map(ScaledMap::getBasicHeight)
							.map(
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

						mapService.updateParticipantPosition(item.getModelObject(), (int) (left / displayFactor), (int) (top / displayFactor));

						redrawMap(target);

					}
				})).add(new DependentOnClickBehavior<>(item.getModel()) {
					private static final long serialVersionUID = 5502865667110141675L;

					@Override
					protected void onClick(AjaxRequestTarget target, ClickEvent event, InitiativeParticipant participant) {
						onParticipantClicked(participant);
						refreshMenus(target);
					}
				});

				item.add(image);
			}
		}.setReuseItems(true));

		DungeonMasterNoteFilter noteFilter = new DungeonMasterNoteFilter();
		if (map != null) {
			noteFilter.map(map);
		} else {
			// Non-nullable, so empty list
			noteFilter.map().isNull();
		}

		preview.add(new DataView<DungeonMasterNote>("notes", FilterDataProvider.of(noteFilter, noteDAO)) {
			private static final long serialVersionUID = -4700768875775660965L;

			@Override
			protected void populateItem(Item<DungeonMasterNote> item) {
				int wh = Optional.ofNullable(map).map(ScaledMap::getSquareSize).map(i -> i / 2).orElse(0);

				Label image = new Label("note", "");
				image.add(AttributeModifier.replace("style", new LoadableDetachableModel<String>() {
					private static final long serialVersionUID = 1L;

					@Override
					protected String load() {
						DungeonMasterNote note = item.getModelObject();

						int left = Option.of(note.getOffsetX()).map(Math::abs).orElse(() -> Option
							.of(map)
							.map(ScaledMap::getBasicWidth)
							.map(
								width -> (int) (width * displayFactor / 2))).getOrElse(0);
						int top = Option.of(note.getOffsetY()).map(Math::abs).orElse(() -> Option
							.of(map)
							.map(ScaledMap::getBasicHeight)
							.map(
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
								+ "#%5$s; text-align: center; word-break: "
								+ "break-all; vertical-align: middle; display: "
								+ "table-cell; color: #cccccc; "
								+ "background-image: url('%4$s'); background-size: "
								+ "%3$dpx %3$dpx;", left, top, actualWH,
							UrlUtils.rewriteToContextRelative("img/note.png",
															  RequestCycle.get()),
							note.color().orElse("000000"));
					}

				}));


				image.add(new DependentOnClickBehavior<>(item.getModel()) {
					private static final long serialVersionUID = -5156716010790702323L;

					@Override
					protected void onClick(AjaxRequestTarget target, ClickEvent event, DungeonMasterNote note) {
						onNoteClicked(note);

						createModalWindow(target, ViewNoteWindow::new, note);

						refreshMenus(target);
					}
				}.withoutPropagation());
				item.add(image);
			}
		});

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
		explorationNavigator.add(new AjaxLink<>("sessionlog", ModelMaker.wrap(view)) {
			private static final long serialVersionUID = -5342617572033020429L;

			@Override
			public void onClick(AjaxRequestTarget target) {

				createModalWindow(target, AddToSessionLogWindow::new, null);
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
	}

	@Override
	public MapView getView() {
		return viewModel.getObject();
	}

	public void onClick(OnClickBehavior.ClickEvent event, AjaxRequestTarget target) {
		onLocationClicked(new Point((int) (event.getOffsetLeft() / displayFactor), (int)
			(event.getOffsetTop() / displayFactor)));

		refreshMenus(target);
	}

	@Override
	public void redrawMap(AjaxRequestTarget target) {
		resetState();

		if (preview instanceof AbstractMapPreview) {
			((AbstractMapPreview) preview).refresh(target);
		}
	}

	@Override
	protected List<Component> getMenuComponents() {
		return List.of(explorationNavigator, tokenStatusPanel, hideReveal);
	}


	@Override
	protected void onDetach() {
		super.onDetach();
		mapModel.detach();
		viewModel.detach();
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
