/**
 * This file is part of Beholder
 * (C) 2016 Jeroen Steenbeeke
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

package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeroensteenbeeke.hyperion.heinlein.web.resources.TouchPunchJavaScriptReference;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.entities.*;
import com.jeroensteenbeeke.topiroll.beholder.web.components.MapCanvas;
import com.jeroensteenbeeke.topiroll.beholder.web.components.OnClickBehavior;
import com.jeroensteenbeeke.topiroll.beholder.web.components.SubmitPanel;
import com.jeroensteenbeeke.topiroll.beholder.web.components.mapcontrol.*;

public class ControlViewPage extends AuthenticatedPage {
	private static final Logger log = LoggerFactory
			.getLogger(ControlViewPage.class);

	private static final String CONTROLLER_ID = "controller";

	private static final long serialVersionUID = 1L;

	private IModel<MapView> viewModel;

	private WebMarkupContainer controller;

	private AjaxLink<Void> markersLink;

	private AjaxLink<Void> moveTokenLink;

	private AjaxLink<Void> tokenStateLink;

	private AjaxLink<Void> hideRevealLink;

	private AjaxLink<Void> createTokensLink;

	private AjaxLink<Void> initiativeLink;

	public ControlViewPage(MapView view) {
		super(String.format("Control View - %s", view.getIdentifier()));

		viewModel = ModelMaker.wrap(view);
		MapCanvas mapCanvas = new MapCanvas("preview", viewModel, true);

		mapCanvas.add(new OnClickBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onClick(AjaxRequestTarget target, ClickEvent event) {
				super.onClick(target, event);

				int offsetTop = event.getOffsetTop();
				int offsetLeft = event.getOffsetLeft();

				MapView view = viewModel.getObject();
				if (view != null) {
					ScaledMap map = view.getSelectedMap();

					if (map != null) {
						double factor = map.getPreviewFactor();

						int x = (int) (offsetLeft / factor);
						int y = (int) (offsetTop / factor);

						log.info("Clicked {},{}", x, y);

						List<Long> selectedShapes = map.getFogOfWarShapes()
								.stream().filter(s -> s.getGroup() == null)
								.filter(s -> s.containsCoordinate(x, y))
								.map(FogOfWarShape::getId)
								.collect(Collectors.toList());

						List<Long> selectedGroups = map.getGroups().stream()
								.filter(s -> s.containsCoordinate(x, y))
								.map(FogOfWarGroup::getId)
								.collect(Collectors.toList());

						log.info("Location matched {} groups and {} shapes",
								selectedGroups.size(), selectedShapes.size());

						if (!selectedShapes.isEmpty()
								|| !selectedGroups.isEmpty()) {
							WebMarkupContainer newController = new HideRevealController(
									CONTROLLER_ID, viewModel.getObject(), map,
									selectedGroups, selectedShapes);
							setController(target, newController);
						} else {
							WebMarkupContainer newController = new HideRevealController(
									CONTROLLER_ID, viewModel.getObject(), map);
							setController(target, newController);
						}
					}
				}

			}
		});
		add(mapCanvas);

		add(new Link<Void>("back") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(OverviewPage.class);
			}
		});

		if (view.getSelectedMap() == null) {
			add(controller = new MapSelectController(CONTROLLER_ID, getUser(),
					view) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onMapSelected(@Nullable ScaledMap map,
						@Nonnull AjaxRequestTarget target) {
					if (map != null) {
						WebMarkupContainer newController = new HideRevealController(
								CONTROLLER_ID, viewModel.getObject(), map);
						setController(target, newController);
					}
				}
			});
		} else {
			add(controller = new HideRevealController(CONTROLLER_ID, view,
					view.getSelectedMap()));
		}

		add(new AjaxLink<Void>("mapSelect") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				if (!(controller instanceof MapSelectController)) {
					WebMarkupContainer newController = new MapSelectController(
							CONTROLLER_ID, getUser(), viewModel.getObject()) {
						private static final long serialVersionUID = 1L;

						@Override
						public void onMapSelected(@Nullable ScaledMap map,
								@Nonnull AjaxRequestTarget target) {
							if (map != null) {
								WebMarkupContainer newController = new HideRevealController(
										CONTROLLER_ID, viewModel.getObject(),
										map);
								setController(target, newController);
							}
						}
					};
					setController(target, newController);
				}

			}
		});

		addLink(hideRevealLink = new AjaxLink<Void>("hideReveal") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				if (!(controller instanceof HideRevealController)) {
					MapView view = viewModel.getObject();
					ScaledMap map = view.getSelectedMap();

					if (map != null) {
						WebMarkupContainer newController = new HideRevealController(
								CONTROLLER_ID, view, map);
						setController(target, newController);

					}
				}
			}
		});

		addLink(tokenStateLink = new AjaxLink<Void>("tokenStates") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				if (!(controller instanceof TokenStateController)) {
					MapView view = viewModel.getObject();
					ScaledMap map = view.getSelectedMap();

					if (map != null) {
						WebMarkupContainer newController = new ControlViewTokenStateController(
								CONTROLLER_ID, view, map);
						setController(target, newController);
					}
				}

			}
		});
		addLink(moveTokenLink = new AjaxLink<Void>("moveTokens") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				if (!(controller instanceof MoveTokenController)) {
					MapView view = viewModel.getObject();
					ScaledMap map = view.getSelectedMap();

					if (map != null) {
						WebMarkupContainer newController = new MoveTokenController(
								CONTROLLER_ID, view, map);
						setController(target, newController);
					}
				}

			}
		});
		addLink(markersLink = new AjaxLink<Void>("markers") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				if (!(controller instanceof MarkerController)) {
					MapView view = viewModel.getObject();

					WebMarkupContainer newController = new ControlViewMarkerController(
							CONTROLLER_ID, view);
					setController(target, newController);

				}

			}
		});
		addLink(createTokensLink = new AjaxLink<Void>("addTokens") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				ScaledMap selectedMap = viewModel.getObject().getSelectedMap();

				if (selectedMap == null) {
					return;
				}

				setResponsePage(new AddTokenInstance1Page(selectedMap) {

					private static final long serialVersionUID = 1L;

					@Override
					protected void onBackButtonClicked() {
						setResponsePage(
								new ControlViewPage(viewModel.getObject()));
					}

					@Override
					protected AddTokenInstance2Page createSecondStepPage(
							ScaledMap map, TokenDefinition token, int current,
							int amount) {
						final IModel<TokenDefinition> tokenModel = ModelMaker
								.wrap(token);
						tokenModel.detach();

						return new AddTokenInstance2Page(map, token,
								TokenBorderType.Enemy, current, amount) {

							private static final long serialVersionUID = 1L;

							@Override
							protected void onBackButtonClicked() {
								setResponsePage(new ControlViewPage(
										viewModel.getObject()));
							}

							@Override
							protected void createSubmitPanel(int current,
									int total, Form<ScaledMap> configureForm) {
								if (current == total) {
									add(new SubmitPanel<>("submit",
											configureForm, m -> {
												setResponsePage(
														new ControlViewPage(
																viewModel
																		.getObject()));
											}));
								} else {
									add(new SubmitPanel<>("submit",
											configureForm, m -> {
												setResponsePage(
														createSecondStepPage(m,
																tokenModel
																		.getObject(),
																current + 1,
																total));
											}));
								}
							}
						};
					}
				});
			}
		});
		addLink(tokenStateLink = new AjaxLink<Void>("initiative") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				if (!(controller instanceof InitiativeOrderController)) {
					MapView view = viewModel.getObject();
					WebMarkupContainer newController = new InitiativeOrderController(
							CONTROLLER_ID, view);
					setController(target, newController);
				}

			}
		});
	}

	public void addLink(AjaxLink<Void> link) {
		link.setOutputMarkupId(true);
		link.setOutputMarkupPlaceholderTag(true);
		// link.setVisible(false);
		add(link);
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		viewModel.detach();
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);

		response.render(JavaScriptHeaderItem
				.forReference(TouchPunchJavaScriptReference.get()));
	}

	private void setController(AjaxRequestTarget target,
			WebMarkupContainer newController) {
		controller.replaceWith(newController);
		target.add(newController);
		controller = newController;

		Stream.<AjaxLink<Void>> builder().add(markersLink).add(moveTokenLink)
				.add(tokenStateLink).add(hideRevealLink).build()
				.forEach(link -> {
					// link.setVisible(mapButtonsVisible);
					target.add(link);
				});
	}

	public class ControlViewMarkerController extends MarkerController {

		private static final long serialVersionUID = 1L;

		private ControlViewMarkerController(String id, MapView view) {
			super(id, view);
		}

		@Override
		public void replaceMe(AjaxRequestTarget target) {
			setController(target, new ControlViewMarkerController(CONTROLLER_ID,
					viewModel.getObject()));

		}

	}

	public class ControlViewTokenStateController extends TokenStateController {
		private static final long serialVersionUID = 1L;

		private ControlViewTokenStateController(String id, MapView view,
				ScaledMap map) {
			super(id, view, map);
		}

		@Override
		public final void replaceMe(AjaxRequestTarget target) {
			setController(target, new ControlViewTokenStateController(
					CONTROLLER_ID, viewModel.getObject(), getMap()));
		}

		@Override
		public void onMarkerCreated(AjaxRequestTarget target) {
			setController(target, new ControlViewMarkerController(CONTROLLER_ID,
					viewModel.getObject()));
		}
	}
}
