package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;

import com.jeroensteenbeeke.hyperion.ducktape.web.resources.ThumbnailResource;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.AjaxIconLink;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.BootstrapPagingNavigator;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.GlyphIcon;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.ScaledMapDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.ScaledMapFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.components.MapCanvas;

public class ControlViewPage extends AuthenticatedPage {

	private static final long serialVersionUID = 1L;

	@Inject
	private ScaledMapDAO mapDAO;
	
	@Inject
	private MapService mapService;

	private IModel<MapView> viewModel;

	public ControlViewPage(MapView view) {
		super(String.format("Control View - %s", view.getIdentifier()));

		viewModel = ModelMaker.wrap(view);
		add(new MapCanvas("preview", viewModel, true));

		ScaledMapFilter mapFilter = new ScaledMapFilter();
		mapFilter.name().orderBy(true);

		DataView<ScaledMap> mapView = new DataView<ScaledMap>("maps",
				FilterDataProvider.of(mapFilter, mapDAO)) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<ScaledMap> item) {
				ScaledMap map = item.getModelObject();

				item.add(new Label("name", map.getName()));
				item.add(new Image("thumb",
						new ThumbnailResource(128, map.getData())));
				item.add(new AjaxIconLink<ScaledMap>("select", item.getModel(), GlyphIcon.screenshot) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						mapService.selectMap(viewModel.getObject(), getModelObject());
					}
				});
			}

		};

		mapView.setItemsPerPage(10);
		add(mapView);
		add(new BootstrapPagingNavigator("mapnav", mapView));
		
		add(new AjaxIconLink<MapView>("unselect", viewModel, GlyphIcon.removeCircle) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				mapService.unselectMap(getModelObject());
			}
		});
	}
}