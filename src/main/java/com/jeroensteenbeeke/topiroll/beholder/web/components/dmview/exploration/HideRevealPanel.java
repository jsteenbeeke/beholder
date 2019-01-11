package com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.exploration;

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
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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

	public HideRevealPanel(String id, MapView view, @Nonnull DMViewCallback callback) {
		super(id);

		IByFunctionModel<MapView> viewModel = ModelMaker.wrap(view);
		setModel(viewModel);

		add(new Label("location", new LoadableDetachableModel<String>() {
			private static final long serialVersionUID = 6824004765739149545L;

			@Override
			protected String load() {
				return Optional.ofNullable(callback.getClickedLocation()).map(p -> String.format
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

				Point p = callback.getClickedLocation();

				mapService.gatherPlayerTokens(view, p.x, p.y);

				callback.redrawMap(target);
			}
		});

		add(new AjaxLink<ScaledMap>("hide", viewModel.getProperty(MapView::getSelectedMap)) {

			private static final long serialVersionUID = -5581748300658008081L;

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
			private static final long serialVersionUID = -5439519905348329569L;

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
			private static final long serialVersionUID = -542296264646923581L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				callback.createModalWindow(target, CreateTokenWindow::new, getModelObject());
			}
		});

		add(new ListView<MapLink>("links", loadLinks(callback)) {
			private static final long serialVersionUID = 3285783029914710847L;

			@Override
			protected void populateItem(ListItem<MapLink> item) {
				PageParameters params = new PageParameters();
				MapLink mapLink = item.getModelObject();

				AbstractLink link;

				if (mapLink.getSourceGroup().getMap().equals(mapLink.getTargetGroup().getMap())) {
					link = new AjaxLink<MapLink>("map", item.getModel()) {

						private static final long serialVersionUID = 2468854582739384695L;

						@Override
						public void onClick(AjaxRequestTarget target) {
							FogOfWarGroup group = getModelObject().getTargetGroup();
							mapService.focusOnGroup(viewModel.getObject(), group);

							double displayFactor = group.getMap().getDisplayFactor(view);

							Integer x = group.getShapes().stream()
									.map(s -> s.visit(new FogOfWarShapeXCoordinateVisitor()))
									.min(Comparator.naturalOrder())
									.map(i -> (int) (i * displayFactor))
									.orElse(null);

							Integer y = group.getShapes().stream()
									.map(s -> s.visit(new FogOfWarShapeYCoordinateVisitor()))
									.min(Comparator.naturalOrder())
									.map(i -> (int) (i * displayFactor))
									.orElse(null);

							StringBuilder script = new StringBuilder();
							script.append("var w = Math.max(document.documentElement.clientWidth, window.innerWidth || 0);\n");
							script.append("var h = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);\n");
							script.append(String.format("window.scrollTo(%d - (w / 4), %d - (h / 4));", x, y));

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

				link.setBody(item.getModel().map(MapLink::getTargetGroup).map(group -> String.format("Transition to %s in %s", group.getName(), group.getMap().getName())));

				item.add(link);
			}
		});
	}

	@Nonnull
	private IModel<List<MapLink>> loadLinks(DMViewCallback callback) {
		return () -> {
			Point location = callback.getClickedLocation();

			ScaledMap map = getModelObject().getSelectedMap();

			if (map != null && location != null) {
				return shapesInCurrentLocation(location, map).flatMap(s -> {
					if (s.getGroup() != null) {
						return s.getGroup().getLinks();
					}

					return Array.empty();
				}).sorted(Comparator.comparing(mapLink -> mapLink.getTargetGroup().getMap().getNameWithFolders())).distinctBy(MapLink::getId)
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

	private Seq<FogOfWarShape> shapesInCurrentLocation(@Nonnull Point currentLocation, @Nullable ScaledMap map) {
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
}
