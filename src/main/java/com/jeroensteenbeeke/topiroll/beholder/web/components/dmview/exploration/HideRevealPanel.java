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
package com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.exploration;

import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarGroupVisibilityDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarShapeDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarShapeVisibilityDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.FogOfWarGroupVisibilityFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.FogOfWarShapeFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.FogOfWarShapeVisibilityFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewCallback;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewPanel;
import com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.CreateTokenWindow;
import com.jeroensteenbeeke.topiroll.beholder.web.data.visitors.FogOfWarShapeContainsVisitor;
import com.jeroensteenbeeke.topiroll.beholder.web.data.visitors.FogOfWarShapeXCoordinateVisitor;
import com.jeroensteenbeeke.topiroll.beholder.web.data.visitors.FogOfWarShapeYCoordinateVisitor;
import io.vavr.collection.Array;
import io.vavr.collection.Seq;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class HideRevealPanel extends DMViewPanel<MapView> {
	@Inject
	private FogOfWarShapeDAO shapeDAO;

	@Inject
	private FogOfWarGroupVisibilityDAO groupVisibilityDAO;

	@Inject
	private FogOfWarShapeVisibilityDAO shapeVisibilityDAO;


	@Inject
	private MapService mapService;

	public HideRevealPanel(String id, MapView view, @NotNull DMViewCallback callback) {
		super(id);

		IModel<MapView> viewModel = ModelMaker.wrap(view);
		setModel(viewModel);

		add(new Label("location", new LoadableDetachableModel<String>() {
			private static final long serialVersionUID = 6824004765739149545L;

			@Override
			protected String load() {
				return callback.getClickedLocation().map(p -> String.format
					("(%d, %d)", p.x, p.y)).orElse("-");
			}
		}));

		add(new AjaxLink<InitiativeParticipant>("gather") {
			private static final long serialVersionUID = -9126220235414475907L;
			@Inject
			private MapService mapService;

			@Override
			public void onClick(AjaxRequestTarget target) {
				MapView view = HideRevealPanel.this.getModelObject();

				callback.getClickedLocation().ifPresent(p -> {
					mapService.gatherPlayerTokens(view, p.x, p.y);

					callback.redrawMap(target);
				});
			}
		});

		add(new ListView<>("hidereveal", new HideRevealOptions(viewModel, callback)) {
			private static final long serialVersionUID = -75420374360776148L;

			@Override
			protected void populateItem(ListItem<HideRevealOption> item) {
				HideRevealOption hideRevealOption = item.getModelObject();

				FogOfWarGroup group = hideRevealOption.getGroup();
				boolean visible = hideRevealOption.isVisible();

				AjaxLink<HideRevealOption> link = new AjaxLink<HideRevealOption>("link", item.getModel()) {
					private static final long serialVersionUID = 8396322701647571238L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						if (visible) {
							mapService.setGroupVisibility(viewModel.getObject(), getModelObject().getGroup(), VisibilityStatus.INVISIBLE);
						} else {
							mapService.setGroupVisibility(viewModel.getObject(), getModelObject().getGroup(), VisibilityStatus.VISIBLE);
						}

						callback.refreshMenus(target);
						callback.redrawMap(target);
					}
				};

				String hideReveal = visible ? "Hide" : "Reveal";

				link.setBody(Model.of(String.format("%s %s", hideReveal, group.getName())));

				item.add(link);
			}
		});

		add(new AjaxLink<>("newtoken", viewModel.map(MapView::getSelectedMap)) {
			private static final long serialVersionUID = -542296264646923581L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				callback.createModalWindow(target, CreateTokenWindow::new,
					getModelObject());
			}
		});

		add(new AjaxLink<>("newnote", viewModel.map(MapView::getSelectedMap)) {
			private static final long serialVersionUID = -542296264646923581L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				callback.createModalWindow(target, CreateNoteWindow::new,
					getModelObject());
			}
		});

		add(new ListView<>("links", loadLinks(callback)) {
			private static final long serialVersionUID = 3285783029914710847L;

			@Override
			protected void populateItem(ListItem<MapLink> item) {
				PageParameters params = new PageParameters();
				MapLink mapLink = item.getModelObject();

				AbstractLink link;

				if (mapLink.getSourceGroup().getMap()
					.equals(mapLink.getTargetGroup().getMap())) {
					link = new AjaxLink<>("map", item.getModel()) {

						private static final long serialVersionUID = 2468854582739384695L;

						@Override
						public void onClick(AjaxRequestTarget target) {
							FogOfWarGroup group = getModelObject().getTargetGroup();
							mapService.focusOnGroup(viewModel.getObject(), group);

							double displayFactor = group.getMap().getDisplayFactor(view);

							Integer x = group.getShapes().stream().map(s -> s.visit(new FogOfWarShapeXCoordinateVisitor()))
								.min(Comparator.naturalOrder())
								.map(i -> (int) (i * displayFactor)).orElse(null);

							Integer y = group.getShapes().stream().map(s -> s.visit(new FogOfWarShapeYCoordinateVisitor()))
								.min(Comparator.naturalOrder())
								.map(i -> (int) (i * displayFactor)).orElse(null);

							StringBuilder script = new StringBuilder();
							script.append(
								"var w = Math.max(document.documentElement.clientWidth, window.innerWidth || 0);\n");
							script.append(
								"var h = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);\n");
							script.append(String.format(
								"window.scrollTo(%d - (w / 4), %d - (h / 4));",
								x, y));

							target.appendJavaScript(script);

							callback.refreshMenus(target);
							callback.redrawMap(target);
						}
					};

				} else {

					params.set("group", mapLink.getTargetGroup().getId());
					params.set("view", viewModel.getObject().getId());

					link = new BookmarkablePageLink<>("map",
						ExplorationModeMapSwitchHandlerPage.class, params);
				}

				link.setBody(item.getModel().map(MapLink::getTargetGroup).map(
					group -> String.format("Transition to %s in %s", group.getName(),
						group.getMap().getName())));

				item.add(link);
			}
		});
	}

	@NotNull
	private IModel<List<MapLink>> loadLinks(DMViewCallback callback) {
		return () -> callback.getClickedLocation().map(location -> {

			ScaledMap map = getModelObject().getSelectedMap();

			if (map != null) {
				return shapesInCurrentLocation(location, map)
					.flatMap(s -> {
						if (s.getGroup() != null) {
							return s.getGroup().getLinks();
						}

						return Array.empty();
					})
					.sorted(Comparator.comparing(mapLink -> mapLink.getTargetGroup().getMap().getNameWithFolders()))
					.distinctBy(MapLink::getId)
					.toJavaList();
			}

			return List.<MapLink>of();
		}).orElseGet(List::of);
	}


	private boolean isShapeInCurrentLocation(Point currentLocation, Predicate<VisibilityStatus> statusPredicate) {
		return Optional.ofNullable(getModelObject()).map(MapView::getSelectedMap).map(
			map -> {


				Seq<VisibilityStatus> shapes = shapesInCurrentLocation(currentLocation, map).flatMap(
					s -> {
						FogOfWarGroup group = s.getGroup();
						if (group != null) {
							return groupVisibilityDAO
								.findByFilter(new FogOfWarGroupVisibilityFilter().group(group))
								.filter(v -> v.getView().equals(getModelObject()));
						}

						return shapeVisibilityDAO
							.findByFilter(new FogOfWarShapeVisibilityFilter().shape(s))
							.filter(v -> v.getView().equals(getModelObject()));
					}
				).map(FogOfWarVisibility::getStatus).filter(statusPredicate);

				return !shapes.isEmpty();
			}).orElse(false);

	}

	private Seq<FogOfWarShape> shapesInCurrentLocation(@NotNull Point currentLocation, @Nullable ScaledMap map) {
		if (map == null) {
			return Array.empty();
		}

		FogOfWarShapeFilter shapeFilter = new FogOfWarShapeFilter();
		shapeFilter.map(map);

		return shapeDAO.findByFilter(shapeFilter).filter(s -> {
			int x = currentLocation.x;
			int y = currentLocation.y;

			return s.visit(new FogOfWarShapeContainsVisitor(x, y));
		});
	}

	private class HideRevealOptions extends LoadableDetachableModel<List<HideRevealOption>> {
		private static final long serialVersionUID = -2820150093807809428L;

		private final IModel<MapView> viewModel;

		private final DMViewCallback callback;

		private HideRevealOptions(IModel<MapView> viewModel, DMViewCallback callback) {
			this.viewModel = viewModel;
			this.callback = callback;
		}

		@Override
		protected List<HideRevealOption> load() {
			return callback.getClickedLocation().map(location -> {
				ScaledMap map = viewModel.getObject().getSelectedMap();

				if (map == null) {
					return List.<HideRevealOption>of();
				}

				FogOfWarShapeFilter shapeFilter = new FogOfWarShapeFilter();
				shapeFilter.map(map);

				return shapeDAO
					.findByFilter(shapeFilter)
					.filter(shape -> shape.visit(new FogOfWarShapeContainsVisitor(location.x, location.y)))
					.map(FogOfWarShape::getGroup)
					.distinct()
					.map(group -> {
						FogOfWarGroupVisibilityFilter visFilter = new FogOfWarGroupVisibilityFilter();
						visFilter.group(group);
						visFilter.view(viewModel.getObject());

						if (groupVisibilityDAO.countByFilter(visFilter) == 0) {
							return new HideRevealOption(group, false);
						} else {
							return new HideRevealOption(group, groupVisibilityDAO
								.getUniqueByFilter(visFilter)
								.map(vis -> vis.getStatus() != VisibilityStatus.INVISIBLE)
								.getOrElse(false));
						}
					})
					.toJavaList();
			}).orElseGet(List::of);
		}

		@Override
		protected void onDetach() {
			super.onDetach();
			viewModel.detach();
		}
	}

	private static class HideRevealOption implements IDetachable {
		private static final long serialVersionUID = 1924134348867864526L;

		private final IModel<FogOfWarGroup> groupModel;

		private final boolean visible;

		public HideRevealOption(@NotNull FogOfWarGroup group, boolean visible) {
			this.groupModel = ModelMaker.wrap(group);
			this.visible = visible;
		}

		@NotNull
		public FogOfWarGroup getGroup() {
			return groupModel.getObject();
		}

		public boolean isVisible() {
			return visible;
		}

		@Override
		public void detach() {
			groupModel.detach();
		}
	}
}
