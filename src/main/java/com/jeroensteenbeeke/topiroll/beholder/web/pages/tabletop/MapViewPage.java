/**
 * This file is part of Beholder
 * (C) 2016 Jeroen Steenbeeke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jeroensteenbeeke.topiroll.beholder.web.pages.tabletop;

import javax.inject.Inject;

import com.jeroensteenbeeke.topiroll.beholder.web.pages.HomePage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.UrlUtils;
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

		WebMarkupContainer container = new WebMarkupContainer("container");
		container.add(AttributeModifier.replace("style",
				String.format("background-image: url('%s');",
						UrlUtils.rewriteToContextRelative("img/fog-of-war.png",
								getRequestCycle()))));
		container.add(new MapCanvas("view", viewModel, false));
		add(container);

	}

	@Override
	protected void onDetach() {
		super.onDetach();
		filter.detach();
	}

}
