package com.jeroensteenbeeke.topiroll.beholder.web.components;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

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

public class MapController extends TypedPanel<ScaledMap> {

	private static final long serialVersionUID = 1L;

	@Inject
	private FogOfWarShapeDAO shapeDAO;

	@Inject
	private FogOfWarGroupDAO groupDAO;

	@Inject
	private MapService mapService;

	private IModel<MapView> mapViewModel;

	public MapController(String id, MapView mapView, ScaledMap map) {
		super(id, ModelMaker.wrap(map));

		this.mapViewModel = ModelMaker.wrap(mapView);

		FogOfWarGroupFilter groupFilter = new FogOfWarGroupFilter();
		groupFilter.map().set(map);
		groupFilter.name().orderBy(true);

		add(new VisibilityControlView<FogOfWarGroup>("groups",
				FilterDataProvider.of(groupFilter, groupDAO)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void applyStatus(FogOfWarGroup group,
					VisibilityStatus status) {
				mapService.setGroupVisibility(mapViewModel.getObject(), group,
						status);
			}
		});

		FogOfWarShapeFilter shapeFilter = new FogOfWarShapeFilter();
		shapeFilter.map().set(map);
		shapeFilter.group().isNull();

		add(new VisibilityControlView<FogOfWarShape>("shapes",
				FilterDataProvider.of(shapeFilter, shapeDAO)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void applyStatus(FogOfWarShape shape,
					VisibilityStatus status) {
				mapService.setShapeVisibility(mapViewModel.getObject(), shape,
						status);
			}
		});

		setOutputMarkupId(true);
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

		@Override
		protected void populateItem(Item<T> item) {
			T shape = item.getModelObject();

			item.add(new NonCachingImage("thumb",
					shape.createThumbnailResource(200)));
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
	};

}
