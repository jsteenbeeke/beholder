/**
 * This file is part of Beholder
 * (C) 2016 Jeroen Steenbeeke
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jeroensteenbeeke.topiroll.beholder.web.components.mapcontrol;

import com.google.common.collect.ImmutableList;
import com.jeroensteenbeeke.hyperion.ducktape.web.components.TypedPanel;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.AjaxIconLink;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.GlyphIcon;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarGroupDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarShapeDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.FogOfWarGroupFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.FogOfWarShapeFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.components.AbstractMapPreview;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.FogOfWarPreviewRenderer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class HideRevealController extends TypedPanel<ScaledMap> implements IClickListener {

	private static final long serialVersionUID = 1L;

	@Inject
	private FogOfWarShapeDAO shapeDAO;

	@Inject
	private FogOfWarGroupDAO groupDAO;

	@Inject
	private MapService mapService;

	private IModel<MapView> mapViewModel;

	public HideRevealController(String id, MapView mapView, ScaledMap map) {
		this(id, mapView, map, ImmutableList.of(), ImmutableList.of());
	}

	public HideRevealController(String id, MapView mapView, ScaledMap map, List<Long> groupIds, List<Long> shapeIds) {
		super(id, ModelMaker.wrap(map));
		setOutputMarkupId(true);

		this.mapViewModel = ModelMaker.wrap(mapView);

		drawListViews(map, groupIds, shapeIds);

		setOutputMarkupId(true);
	}

	private void drawListViews(ScaledMap map, List<Long> groupIds, List<Long> shapeIds) {
		FogOfWarGroupFilter groupFilter = new FogOfWarGroupFilter();
		if (!groupIds.isEmpty()) {
			groupFilter.id().in(groupIds);
		}
		groupFilter.map().set(map);
		groupFilter.name().orderBy(true);

		addOrReplace(new VisibilityControlView<FogOfWarGroup>("groups",
				FilterDataProvider.of(groupFilter, groupDAO)) {
			private static final long serialVersionUID = 1L;

			@Override
			public List<FogOfWarShape> getShapes(FogOfWarGroup object) {
				return object.getShapes();
			}

			@Override
			public void applyStatus(FogOfWarGroup group,
									VisibilityStatus status) {
				mapService.setGroupVisibility(mapViewModel.getObject(), group,
						status);
			}
		});

		FogOfWarShapeFilter shapeFilter = new FogOfWarShapeFilter();
		if (!shapeIds.isEmpty()) {
			shapeFilter.id().in(shapeIds);
		}
		shapeFilter.map().set(map);
		shapeFilter.group().isNull();

		addOrReplace(new VisibilityControlView<FogOfWarShape>("shapes",
				FilterDataProvider.of(shapeFilter, shapeDAO)) {
			private static final long serialVersionUID = 1L;

			@Override
			public List<FogOfWarShape> getShapes(FogOfWarShape object) {
				return ImmutableList.of(object);
			}

			@Override
			public void applyStatus(FogOfWarShape shape,
									VisibilityStatus status) {
				mapService.setShapeVisibility(mapViewModel.getObject(), shape,
						status);
			}
		});
	}

	@Override
	public void onClick(AjaxRequestTarget target, ScaledMap map, int x, int y) {

		List<Long> selectedShapes = map.getFogOfWarShapes()
				.stream().filter(s -> s.getGroup() == null)
				.filter(s -> s.containsCoordinate(x, y))
				.map(FogOfWarShape::getId)
				.collect(Collectors.toList());

		List<Long> selectedGroups = map.getGroups().stream()
				.filter(s -> s.containsCoordinate(x, y))
				.map(FogOfWarGroup::getId)
				.collect(Collectors.toList());

		if (!selectedShapes.isEmpty()
				|| !selectedGroups.isEmpty()) {
			drawListViews(map, selectedGroups, selectedGroups);
			target.add(this);
		}
	}

	@Override
	protected void onDetach() {
		super.onDetach();

		mapViewModel.detach();
	}

	private abstract class VisibilityControlView<T extends ICanHazVisibilityStatus>
			extends DataView<T> {
		private static final long serialVersionUID = 1L;

		protected VisibilityControlView(String id,
										IDataProvider<T> dataProvider) {
			super(id, dataProvider);
		}

		@SuppressWarnings("unchecked")
		public void setStatus(Item<T> item, VisibilityStatus status,
							  AjaxRequestTarget target) {
			final AjaxIconLink<T> hideLink = (AjaxIconLink<T>) item.get("hide");
			final AjaxIconLink<T> dmLink = (AjaxIconLink<T>) item.get("dm");
			final AjaxIconLink<T> showLink = (AjaxIconLink<T>) item.get("show");

			final T object = item.getModelObject();

			applyStatus(object, status);

			updateVisibility(target, object, hideLink, dmLink, showLink);

		}

		public abstract void applyStatus(T object, VisibilityStatus status);

		public abstract List<FogOfWarShape> getShapes(T object);

		@Override
		protected void populateItem(Item<T> item) {
			T shape = item.getModelObject();

			item.add(new AbstractMapPreview("thumb", mapViewModel.getObject().getSelectedMap(), 128) {
				@Override
				protected void addOnDomReadyJavaScript(String canvasId, StringBuilder js, double factor) {
					getShapes(item.getModelObject()).stream()
							.map(s -> s.visit(new FogOfWarPreviewRenderer(canvasId, factor)))
							.forEach(js::append);
				}
			});
			item.add(new Label("description", shape.getDescription()));

			AjaxIconLink<T> hideLink = new AjaxIconLink<T>("hide",
					item.getModel(), GlyphIcon.eyeClose) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					setStatus(item, VisibilityStatus.INVISIBLE, target);
				}
			};
			hideLink.setOutputMarkupPlaceholderTag(true);
			item.add(hideLink);

			AjaxIconLink<T> dmLink = new AjaxIconLink<T>("dm", item.getModel(),
					GlyphIcon.search) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					setStatus(item, VisibilityStatus.DM_ONLY, target);
				}
			};
			dmLink.setOutputMarkupPlaceholderTag(true);
			item.add(dmLink);

			AjaxIconLink<T> showLink = new AjaxIconLink<T>("show",
					item.getModel(), GlyphIcon.eyeOpen) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					setStatus(item, VisibilityStatus.VISIBLE, target);
				}
			};
			showLink.setOutputMarkupPlaceholderTag(true);
			item.add(showLink);
			updateVisibility(null, shape, hideLink, dmLink, showLink);

		}

		private void updateVisibility(AjaxRequestTarget target, T shape,
									  AjaxIconLink<T> hideLink, AjaxIconLink<T> dmLink,
									  AjaxIconLink<T> showLink) {
			MapView mapView = mapViewModel.getObject();
			hideLink.setVisibilityAllowed(
					shape.getStatus(mapView) != VisibilityStatus.INVISIBLE);
			dmLink.setVisibilityAllowed(
					shape.getStatus(mapView) != VisibilityStatus.DM_ONLY);
			showLink.setVisibilityAllowed(
					shape.getStatus(mapView) != VisibilityStatus.VISIBLE);

			if (target != null) {
				target.add(hideLink, dmLink, showLink);
			}
		}
	}

	;

}
