package com.jeroensteenbeeke.topiroll.beholder.web.components.mapcontrol;

import javax.inject.Inject;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;

import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.topiroll.beholder.dao.AreaMarkerDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.AreaMarker;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.AreaMarkerFilter;

public class MarkerController extends Panel {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private AreaMarkerDAO markerDAO;

	public MarkerController(String id, MapView view,
			ScaledMap map) {
		super(id);
		
		AreaMarkerFilter filter = new AreaMarkerFilter();
		filter.view().set(view);
		
		add(new DataView<AreaMarker>("markers", FilterDataProvider.of(filter, markerDAO)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<AreaMarker> item) {
				AreaMarker marker = item.getModelObject();
				
				item.add(marker.createPanel("marker"));
				
			}
		});
	}

}
