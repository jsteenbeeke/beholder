package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import javax.inject.Inject;

import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

import com.jeroensteenbeeke.topiroll.beholder.dao.MapViewDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.MapViewFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.components.MapCanvas;

public class MapViewPage extends WebPage {
	private static final long serialVersionUID = 1L;

	@Inject
	private MapViewDAO viewDAO;

	private MapViewFilter filter;


	public MapViewPage(PageParameters params) {
		StringValue identifier = params.get("identifier");
		if (identifier.isNull() || identifier.isEmpty()) {
			throw new RestartResponseAtInterceptPageException(HomePage.class);
		}

		this.filter = new MapViewFilter();
		filter.identifier().set(identifier.toOptionalString());

		IModel<MapView> viewModel = new LoadableDetachableModel<MapView>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected MapView load() {
				return viewDAO.getUniqueByFilter(filter);
			}
		};

		MapView currentView = viewModel.getObject();
		if (currentView == null) {
			throw new RestartResponseAtInterceptPageException(HomePage.class);
		}

		add(new Label("title", "Map View"));

		add(new MapCanvas("view", viewModel, false));

	}

	@Override
	protected void onDetach() {
		super.onDetach();
		filter.detach();
	}

}
