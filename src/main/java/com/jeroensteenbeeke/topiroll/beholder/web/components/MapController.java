package com.jeroensteenbeeke.topiroll.beholder.web.components;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;

import com.jeroensteenbeeke.hyperion.ducktape.web.components.TypedPanel;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.AjaxIconLink;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.GlyphIcon;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarGroupDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarShapeDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarGroup;
import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarShape;
import com.jeroensteenbeeke.topiroll.beholder.entities.ICanHazVisibilityStatus;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.VisibilityStatus;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.FogOfWarGroupFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.FogOfWarShapeFilter;

public class MapController extends TypedPanel<ScaledMap> {

	private static final long serialVersionUID = 1L;

	@Inject
	private FogOfWarShapeDAO shapeDAO;
	
	@Inject
	private FogOfWarGroupDAO groupDAO;

	public MapController(String id, ScaledMap map) {
		super(id, ModelMaker.wrap(map));
		
		FogOfWarGroupFilter groupFilter = new FogOfWarGroupFilter();
		groupFilter.map().set(map);
		groupFilter.name().orderBy(true);
		
		add(new VisibilityControlView<FogOfWarGroup>("groups",
				FilterDataProvider.of(groupFilter, groupDAO)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void applyStatus(FogOfWarGroup object,
					VisibilityStatus status) {
				object.setStatus(status);
				groupDAO.update(object);
			}
		});

		FogOfWarShapeFilter shapeFilter = new FogOfWarShapeFilter();
		shapeFilter.map().set(map);
		shapeFilter.group().isNull();

		add(new VisibilityControlView<FogOfWarShape>("shapes",
				FilterDataProvider.of(shapeFilter, shapeDAO)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void applyStatus(FogOfWarShape object,
					VisibilityStatus status) {
				object.setStatus(status);
				shapeDAO.update(object);
			}
		});
		
		setOutputMarkupId(true);
	}

	private static abstract class VisibilityControlView<T extends ICanHazVisibilityStatus>
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

			item.add(new NonCachingImage("thumb", shape.createThumbnailResource(100)));
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

		private void updateVisibility(AjaxRequestTarget target, T shape, AjaxIconLink<T> hideLink,
				AjaxIconLink<T> dmLink, AjaxIconLink<T> showLink) {
			hideLink.setVisibilityAllowed(
					shape.getStatus() != VisibilityStatus.INVISIBLE);
			dmLink.setVisibilityAllowed(
					shape.getStatus() != VisibilityStatus.DM_ONLY);
			showLink.setVisibilityAllowed(
					shape.getStatus() != VisibilityStatus.VISIBLE);
			
			if (target != null) {
				target.add(hideLink, dmLink, showLink);
			}
		}
	};

}
