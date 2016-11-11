package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import javax.inject.Inject;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.UrlUtils;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.time.Duration;

import com.jeroensteenbeeke.topiroll.beholder.dao.MapViewDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.MapViewFilter;

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

		WebMarkupContainer element = new WebMarkupContainer("view");
		element.setOutputMarkupId(true);
		element.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(5)));
		
		element.add(AttributeModifier.replace("style", new LoadableDetachableModel<String>() {

			private static final long serialVersionUID = 1L;

			@Override
			protected String load() {
				MapView view = viewModel.getObject();
				
				StringBuilder builder = new StringBuilder();
				
				builder.append("width: ").append(view.getWidth()).append("; ");
				builder.append("height: ").append(view.getHeight()).append("; ");
				
				ScaledMap selectedMap = view.getSelectedMap();
				
				if (selectedMap != null) {
					final String rewritten = UrlUtils.rewriteToContextRelative(String.format("maps/%d.png?w=%d&h=%d&d=%d", selectedMap.getId(), view.getWidth(), view.getHeight(), view.getScreenDiagonalInInches()), RequestCycle.get());
					
					builder.append("background-image: url('").append(rewritten).append("');");
				}
				
				return builder.toString();
			}
		}));
		
		add(element);
		
	}
	
	@Override
	protected void onDetach() {
		super.onDetach();
		filter.detach();
		viewModel.detach();
	}

}
