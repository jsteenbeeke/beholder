package com.jeroensteenbeeke.topiroll.beholder.web.components.mapcontrol;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import com.google.common.collect.Lists;
import com.jeroensteenbeeke.hyperion.ducktape.web.components.TypedPanel;
import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeLocation;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;

public class InitiativeOrderController extends TypedPanel<MapView> {

	private static final long serialVersionUID = 1L;

	
	
	public InitiativeOrderController(String id, MapView view) {
		super(id);
		
		
		add(new ListView<InitiativeLocation>("positions", Lists.newArrayList(InitiativeLocation.values())) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<InitiativeLocation> item) {
				item.add(new AjaxLink<InitiativeLocation>("show") {

					private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					
					
				}
				});
			}

			

			
		});
	}

}
