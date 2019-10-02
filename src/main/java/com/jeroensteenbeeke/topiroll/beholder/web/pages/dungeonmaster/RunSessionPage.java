/**
 * This file is part of Beholder
 * (C) 2016-2019 Jeroen Steenbeeke
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
package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster;

import com.jeroensteenbeeke.hyperion.heinlein.web.components.BootstrapPagingNavigator;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.topiroll.beholder.dao.MapViewDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.MapViewFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.play.combat.CombatControllerPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.play.exploration.ExplorationControllerPage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.request.UrlUtils;
import org.apache.wicket.request.cycle.RequestCycle;

import javax.inject.Inject;

public class RunSessionPage extends AuthenticatedPage {



	private static final long serialVersionUID = 1L;

	@Inject
	private MapViewDAO mapViewDAO;

	public  RunSessionPage() {
		super("");

		MapViewFilter viewFilter = new MapViewFilter();
		viewFilter.owner().set(getUser());
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

				final String musicUrl = UrlUtils.rewriteToContextRelative(
						String.format("music/%s", mapView.getIdentifier()),
						RequestCycle.get());

				item.add(new Label("width", mapView.getWidth()));
				item.add(new Label("height", mapView.getHeight()));
				item.add(new Label("diagonal",
						mapView.getScreenDiagonalInInches()));
				item.add(new ExternalLink("player", url)
						.add(AttributeModifier.replace("target", "_blank")));
				item.add(new ExternalLink("music", musicUrl)
						.add(AttributeModifier.replace("target", "_blank")));
				item.add(new Link<>("exploration", item.getModel()) {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						setResponsePage(new ExplorationControllerPage(item.getModelObject()));

					}
				});
				item.add(new Link<>("combat", item.getModel()) {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						setResponsePage(new CombatControllerPage(item.getModelObject()));

					}
				});
			}

		};

		viewView.setItemsPerPage(5);
		add(viewView);
		add(new BootstrapPagingNavigator("viewnav", viewView));
	}

}
