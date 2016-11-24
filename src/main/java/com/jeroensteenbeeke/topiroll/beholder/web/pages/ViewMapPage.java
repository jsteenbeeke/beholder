package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import java.awt.Graphics2D;

import javax.inject.Inject;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;

import com.jeroensteenbeeke.hyperion.heinlein.web.components.BootstrapPagingNavigator;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.GlyphIcon;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.IconTextLink;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarShapeDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarShape;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.FogOfWarShapeFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.resources.AbstractFogOfWarPreviewResource;

public class ViewMapPage extends AuthenticatedPage {
	private static final long serialVersionUID = 1L;

	@Inject
	private FogOfWarShapeDAO shapeDAO;
	
	public ViewMapPage(ScaledMap map) {
		super(String.format("View Map - %s", map.getName()));
		
		add(new Link<Void>("back") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new OverviewPage());
				
			}
		});
		
		add(new Image("preview", new AbstractFogOfWarPreviewResource(ModelMaker.wrap(map)) {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void drawShape(Graphics2D graphics2d) {
				
			}
		}));
		
		FogOfWarShapeFilter filter = new FogOfWarShapeFilter();
		filter.map().set(map);
		
		DataView<FogOfWarShape> shapesView = new DataView<FogOfWarShape>("shapes", FilterDataProvider.of(filter, shapeDAO)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<FogOfWarShape> item) {
				FogOfWarShape shape = item.getModelObject();
				
				item.add(new Label("shape", shape.getDescription()));
				item.add(new Image("thumb", shape.createThumbnailResource(400)));
			}
			
		};
		shapesView.setItemsPerPage(10L);
		
		add(shapesView);
		
		add(new BootstrapPagingNavigator("shapenav", shapesView));
		
		add(new IconTextLink<ScaledMap>("addcircle", ModelMaker.wrap(map), GlyphIcon.plusSign, m -> "Add Circle") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new AddCircleFogOfWarPage(getModelObject()));
				
			}
		});
		
		
		add(new IconTextLink<ScaledMap>("addrect", ModelMaker.wrap(map), GlyphIcon.plusSign, m -> "Add Rectangle") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new AddRectFogOfWarPage(getModelObject()));
				
			}
		});

	}
}
