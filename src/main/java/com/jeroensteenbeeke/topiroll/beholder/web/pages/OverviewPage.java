package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import javax.inject.Inject;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.Link;

import com.jeroensteenbeeke.hyperion.heinlein.web.pages.BSEntityFormPage;
import com.jeroensteenbeeke.topiroll.beholder.dao.MapViewDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.MapViewFilter;

public class OverviewPage extends AuthenticatedPage {

	private static final long serialVersionUID = 1L;

	@Inject
	private MapViewDAO mapViewDAO;

	public OverviewPage() {
		super("Overview");
		
		MapViewFilter filter = new MapViewFilter();
		filter.identifier().orderBy(true);
		
		

		add(new Link<MapView>("addmap") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				MapView view = new MapView();
				view.setWidth(1920);
				view.setHeight(1080);
				
				setResponsePage(
						new BSEntityFormPage<MapView>(create(view)
								.onPage("Create Map View").using(mapViewDAO)) {
							private static final long serialVersionUID = 1L;

							@Override
							protected void onSaved(MapView entity) {
								setResponsePage(new OverviewPage());

							}

							@Override
							protected void onCancel(MapView entity) {
								setResponsePage(new OverviewPage());
							}

						});

			}
		});

	}

	@Override
	public Component createNavComponent(String id) {
		return new WebMarkupContainer(id).setVisible(false);
	}

}
