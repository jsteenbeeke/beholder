package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import javax.inject.Inject;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.UrlUtils;
import org.apache.wicket.request.cycle.RequestCycle;

import com.jeroensteenbeeke.hyperion.ducktape.web.resources.ThumbnailResource;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.BootstrapPagingNavigator;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.GlyphIcon;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.IconLink;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.BSEntityFormPage;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.topiroll.beholder.dao.MapViewDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.ScaledMapDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.MapViewFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.ScaledMapFilter;

public class OverviewPage extends AuthenticatedPage {

	private static final long serialVersionUID = 1L;

	@Inject
	private MapViewDAO mapViewDAO;
	
	@Inject
	private ScaledMapDAO mapDAO;

	public OverviewPage() {
		super("Overview");

		MapViewFilter viewFilter = new MapViewFilter();
		viewFilter.identifier().orderBy(true);

		DataView<MapView> viewView = new DataView<MapView>("views",
				FilterDataProvider.of(viewFilter, mapViewDAO)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<MapView> item) {
				MapView mapView = item.getModelObject();

				item.add(new Label("identifier", mapView.getIdentifier()));
				final String url = UrlUtils.rewriteToContextRelative(
						String.format("views/%s", mapView.getIdentifier()),
						RequestCycle.get());

				item.add(new ExternalLink("url", url).setBody(Model.of(url))
						.add(AttributeModifier.replace("target", "_blank")));
				item.add(new Label("width", mapView.getWidth()));
				item.add(new Label("height", mapView.getHeight()));
				item.add(new Label("diagonal",
						mapView.getScreenDiagonalInInches()));
				item.add(new IconLink<MapView>("control", item.getModel(), GlyphIcon.eyeOpen) {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						setResponsePage(new ControlViewPage(item.getModelObject()));
						
					}
				});

			}

		};

		viewView.setItemsPerPage(5);
		add(viewView);
		add(new BootstrapPagingNavigator("viewnav", viewView));
		
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
						item.add(new IconLink<ScaledMap>("view", item.getModel(), GlyphIcon.eyeOpen) {

							private static final long serialVersionUID = 1L;

							@Override
							public void onClick() {
								setResponsePage(new ViewMapPage(item.getModelObject()));
								
							}
						});
					}
			
		};

		mapView.setItemsPerPage(10);
		add(mapView);
		add(new BootstrapPagingNavigator("mapnav", mapView));


		add(new Link<MapView>("addview") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				MapView view = new MapView();
				view.setWidth(1920);
				view.setHeight(1080);

				setResponsePage(new BSEntityFormPage<MapView>(create(view)
						.onPage("Create Map View").using(mapViewDAO)) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onBeforeSave(MapView entity) {
						entity.setOwner(getUser());
					}

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
		
		add(new Link<ScaledMap>("addmap") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new UploadMapStep1Page());

			}
		});

	}


}
