package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import javax.inject.Inject;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;

import com.jeroensteenbeeke.hyperion.ducktape.web.resources.ThumbnailResource;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.BootstrapPagingNavigator;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.dao.ScaledMapDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.ScaledMapFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.components.MapCanvas;

public class ControlViewPage extends AuthenticatedPage {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private ScaledMapDAO mapDAO;

	public ControlViewPage(MapView view) {
		super(String.format("Control View - %s", view.getIdentifier()));
		
		add(new MapCanvas("preview", ModelMaker.wrap(view), true));
		
		ScaledMapFilter mapFilter = new ScaledMapFilter();
		mapFilter.name().orderBy(true);
		
		DataView<ScaledMap> mapView = new DataView<ScaledMap>("maps",
				FilterDataProvider.of(mapFilter, mapDAO)) {

					private static final long serialVersionUID = 1L;

					@Override
					protected void populateItem(Item<ScaledMap> item) {
						ScaledMap map = item.getModelObject();
						
						item.add(new Label("name", map.getName()));
						item.add(new Image("thumb", new ThumbnailResource(128, map.getData())));
						
					}
			
		};

		mapView.setItemsPerPage(10);
		add(mapView);
		add(new BootstrapPagingNavigator("mapnav", mapView));
	}
}
