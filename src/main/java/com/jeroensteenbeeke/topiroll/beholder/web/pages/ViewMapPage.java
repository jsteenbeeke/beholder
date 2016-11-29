package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import java.awt.Graphics2D;

import javax.inject.Inject;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;

import com.jeroensteenbeeke.hyperion.heinlein.web.components.BootstrapPagingNavigator;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.GlyphIcon;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.IconLink;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarGroupDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarShapeDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarGroup;
import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarShape;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.FogOfWarGroupFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.FogOfWarShapeFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.resources.AbstractFogOfWarPreviewResource;

public class ViewMapPage extends AuthenticatedPage {
	private static final long serialVersionUID = 1L;

	@Inject
	private FogOfWarShapeDAO shapeDAO;

	@Inject
	private FogOfWarGroupDAO groupDAO;

	private IModel<ScaledMap> mapModel;

	public ViewMapPage(ScaledMap map) {
		super(String.format("View Map - %s", map.getName()));

		add(new Link<Void>("back") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new OverviewPage());

			}
		});

		mapModel = ModelMaker.wrap(map);
		add(new NonCachingImage("preview",
				new AbstractFogOfWarPreviewResource(mapModel) {

					private static final long serialVersionUID = 1L;

					@Override
					public void drawShape(Graphics2D graphics2d) {

					}
				}));

		FogOfWarGroupFilter groupFilter = new FogOfWarGroupFilter();
		groupFilter.map().set(map);
		groupFilter.name().orderBy(true);

		DataView<FogOfWarGroup> groupsView = new DataView<FogOfWarGroup>(
				"groups", FilterDataProvider.of(groupFilter, groupDAO)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<FogOfWarGroup> item) {
				FogOfWarGroup group = item.getModelObject();

				item.add(new Label("name", group.getName()));
				item.add(new NonCachingImage("thumb",
						group.createThumbnailResource(200)));
			}
		};
		groupsView.setItemsPerPage(10L);
		add(groupsView);
		add(new BootstrapPagingNavigator("groupnav", groupsView));

		FogOfWarShapeFilter shapeFilter = new FogOfWarShapeFilter();
		shapeFilter.map().set(map);
		shapeFilter.group().isNull();

		DataView<FogOfWarShape> shapesView = new DataView<FogOfWarShape>(
				"shapes", FilterDataProvider.of(shapeFilter, shapeDAO)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<FogOfWarShape> item) {
				FogOfWarShape shape = item.getModelObject();

				item.add(new Label("shape", shape.getDescription()));
				item.add(new NonCachingImage("thumb",
						shape.createThumbnailResource(200)));
				item.add(new IconLink<FogOfWarShape>("delete", item.getModel(),
						GlyphIcon.trash) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						shapeDAO.delete(getModelObject());
						setResponsePage(new ViewMapPage(mapModel.getObject()));
					}
				});
			}

		};
		shapesView.setItemsPerPage(10L);
		add(shapesView);
		add(new BootstrapPagingNavigator("shapenav", shapesView));

		add(new Link<ScaledMap>("addcircle", mapModel) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new AddCircleFogOfWarPage(getModelObject()));

			}
		});

		add(new Link<ScaledMap>("addrect", mapModel) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new AddRectFogOfWarPage(getModelObject()));

			}
		});

		add(new Link<ScaledMap>("group", mapModel) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new CreateGroupPage(getModelObject()));

			}
		});

	}

	@Override
	protected void onDetach() {
		super.onDetach();
		mapModel.detach();
	}
}
