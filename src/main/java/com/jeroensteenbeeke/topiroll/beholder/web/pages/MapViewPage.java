package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import javax.inject.Inject;

import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.time.Duration;

import com.jeroensteenbeeke.topiroll.beholder.dao.MapViewDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.MapViewFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.resources.ToScaleMapResource;

public class MapViewPage extends WebPage {
	private static final long serialVersionUID = 1L;
	
	@Inject
	private MapViewDAO viewDAO;

	private MapViewFilter filter;
	
	private IModel<MapView> viewModel;
	
	public MapViewPage(PageParameters params) {
		StringValue identifier = params.get("identifier");
		if (identifier.isNull() || identifier.isEmpty()) {
			throw new RestartResponseAtInterceptPageException(HomePage.class);
		}
		
		this.filter = new MapViewFilter();
		filter.identifier().set(identifier.toOptionalString());
		
		viewModel = new LoadableDetachableModel<MapView>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected MapView load() {
				return viewDAO.getUniqueByFilter(filter);
			}
		};
		
		if (viewModel.getObject() == null) {
			throw new RestartResponseAtInterceptPageException(HomePage.class);
		}
		
		add(new Label("title", "Map View"));

		Image image = new Image("view", new ToScaleMapResource(viewModel));
		image.setOutputMarkupId(true);
		image.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(5)));
		
		
		
		add(image);
		
	}
	
	@Override
	protected void onDetach() {
		super.onDetach();
		filter.detach();
		viewModel.detach();
	}

}
