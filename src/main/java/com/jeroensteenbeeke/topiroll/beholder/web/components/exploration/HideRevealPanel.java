package com.jeroensteenbeeke.topiroll.beholder.web.components.exploration;

import com.google.common.collect.ImmutableList;
import com.jeroensteenbeeke.hyperion.solstice.data.IByFunctionModel;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarGroupVisibilityDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarShapeDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarShapeVisibilityDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.FogOfWarGroupVisibilityFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.FogOfWarShapeFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.FogOfWarShapeVisibilityFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.exploration.ExplorationControllerPage;
import io.vavr.collection.Array;
import io.vavr.collection.Seq;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class HideRevealPanel extends ExplorationModePanel<MapView> {
	@Inject
	private FogOfWarShapeDAO shapeDAO;

	@Inject
	private FogOfWarGroupVisibilityDAO groupVisibilityDAO;

	@Inject
	private FogOfWarShapeVisibilityDAO shapeVisibilityDAO;


	@Inject
	private MapService mapService;

	public HideRevealPanel(String id, MapView view, @Nonnull ExplorationModeCallback callback) {
		super(id);

		IByFunctionModel<MapView> viewModel = ModelMaker.wrap(view);
		setModel(viewModel);

		add(new Label("location", new LoadableDetachableModel<String>() {
			@Override
			protected String load() {
				return Optional.ofNullable(callback.getClickedLocation()).map(p -> String.format
						("(%d, %d)", p.x, p.y)).orElse("-");
			}
		}));

		add(new AjaxLink<InitiativeParticipant>("gather") {
			@Inject
			private MapService mapService;

			@Override
			public void onClick(AjaxRequestTarget target) {
				MapView view = HideRevealPanel.this.getModelObject();

				Point p = callback.getClickedLocation();

				mapService.gatherPlayerTokens(view, p.x, p.y);

				callback.redrawMap(target);
			}
		});

		add(new AjaxLink<ScaledMap>("hide", viewModel.getProperty(MapView::getSelectedMap)) {

			@Override
			public void onClick(AjaxRequestTarget target) {
				Point clicked = callback.getClickedLocation();

				if (clicked != null) {
					shapesInCurrentLocation(clicked, getModelObject()).filter(s -> s.getGroup() == null).forEach(shape -> mapService.setShapeVisibility(viewModel.getObject(), shape, VisibilityStatus.INVISIBLE));
					shapesInCurrentLocation(clicked, getModelObject()).map(FogOfWarShape::getGroup).filter(Objects::nonNull)
							.forEach(group -> mapService.setGroupVisibility(viewModel.getObject(), group, VisibilityStatus.INVISIBLE));

					callback.redrawMap(target);
				}
			}

			@Override
			public boolean isVisible() {
				boolean v = super.isVisible();

				Point clicked = callback.getClickedLocation();

				if (clicked != null) {
					return v && isShapeInCurrentLocation(clicked, s -> s == VisibilityStatus.DM_ONLY || s == VisibilityStatus.VISIBLE);
				} else {
					return false;
				}
			}
		});

		add(new AjaxLink<ScaledMap>("reveal", viewModel.getProperty(MapView::getSelectedMap)) {
			@Override
			public void onClick(AjaxRequestTarget target) {
				Point clicked = callback.getClickedLocation();

				if (clicked != null) {
					shapesInCurrentLocation(clicked, getModelObject()).filter(s -> s.getGroup() == null).forEach(shape -> mapService.setShapeVisibility(viewModel.getObject(), shape, VisibilityStatus.VISIBLE));
					shapesInCurrentLocation(clicked, getModelObject()).map(FogOfWarShape::getGroup).filter(Objects::nonNull)
							.forEach(group -> mapService.setGroupVisibility(viewModel.getObject(), group, VisibilityStatus.VISIBLE));

					callback.redrawMap(target);
				}
			}


			@Override
			public boolean isVisible() {
				boolean v = super.isVisible();

				Point clicked = callback.getClickedLocation();

				if (clicked != null && !shapesInCurrentLocation(clicked, getModelObject()).isEmpty()) {
					return v && !isShapeInCurrentLocation(clicked, s -> s == VisibilityStatus.DM_ONLY || s == VisibilityStatus.VISIBLE);
				} else {
					return false;
				}
			}
		});


		add(new AjaxLink<ScaledMap>("newtoken", viewModel.getProperty(MapView::getSelectedMap)) {
			@Override
			public void onClick(AjaxRequestTarget target) {
				callback.createModalWindow(target, CreateTokenPanel::new, getModelObject());
			}
		});

		add(new ListView<MapLink>("links", loadLinks(callback)) {
			@Override
			protected void populateItem(ListItem<MapLink> item) {
				PageParameters params = new PageParameters();
				params.set("map", item.getModelObject().getMap().getId());
				params.set("view", viewModel.getObject().getId());

				BookmarkablePageLink<MapLink> link = new BookmarkablePageLink<>("map",
						ExplorationModeMapSwitchHandlerPage.class, params);

				link.setBody(item.getModel().map(MapLink::getMap).map(ScaledMap::getNameWithFolders).map(n -> "Transition to " + n));

				item.add(link);
			}
		});
	}

	@Nonnull
	private IModel<List<MapLink>> loadLinks(ExplorationModeCallback callback) {
		return () -> {
			Point location = callback.getClickedLocation();

			ScaledMap map = getModelObject().getSelectedMap();

			if (map != null && location != null) {
				return shapesInCurrentLocation(location, map).flatMap(s -> {
					if (s.getGroup() != null) {
						return s.getGroup().getLinks();
					}

					return s.getLinks();
				}).sorted(Comparator.comparing(mapLink -> mapLink.getMap().getNameWithFolders())).distinctBy(MapLink::getId)
						.toJavaList();
			}

			return ImmutableList.of();
		};
	}


	private boolean isShapeInCurrentLocation(Point currentLocation, Predicate<VisibilityStatus> statusPredicate) {
		return Optional.ofNullable(getModelObject()).map(MapView::getSelectedMap).map(
				map -> {


					Seq<VisibilityStatus> shapes = shapesInCurrentLocation(currentLocation, map).flatMap(
							s -> {
								FogOfWarGroup group = s.getGroup();
								if (group != null) {
									return groupVisibilityDAO.findByFilter(new FogOfWarGroupVisibilityFilter().group(group)).filter(v -> v.getView().equals(getModelObject()));
								}

								return shapeVisibilityDAO.findByFilter(new FogOfWarShapeVisibilityFilter().shape(s)).filter(v -> v.getView().equals(getModelObject()));
							}
					).map(FogOfWarVisibility::getStatus).filter(statusPredicate);

					return !shapes.isEmpty();
				}).orElse(false);

	}

	private Seq<FogOfWarShape> shapesInCurrentLocation(@Nonnull Point currentLocation, ScaledMap map) {
		FogOfWarShapeFilter shapeFilter = new FogOfWarShapeFilter();
		shapeFilter.map(map);

		return shapeDAO.findByFilter(shapeFilter).filter(s -> {
			int x = currentLocation.x;
			int y = currentLocation.y;

			return s.containsCoordinate(x, y);
		});
	}
}
