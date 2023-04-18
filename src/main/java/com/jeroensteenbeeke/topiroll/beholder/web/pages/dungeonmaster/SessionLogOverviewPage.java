/*
 * This file is part of Beholder
 * Copyright (C) 2016 - 2023 Jeroen Steenbeeke
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
import com.jeroensteenbeeke.hyperion.heinlein.web.components.IconLink;
import com.jeroensteenbeeke.hyperion.icons.fontawesome.FontAwesome;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.topiroll.beholder.dao.SessionLogIndexDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.SessionLogIndex;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.SessionLogIndexFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;

import javax.inject.Inject;

public class SessionLogOverviewPage extends AuthenticatedPage {



	private static final long serialVersionUID = 1L;

	@Inject
	private SessionLogIndexDAO indexDAO;

	public  SessionLogOverviewPage() {
		super("Session Log Overview");

		SessionLogIndexFilter filter = new SessionLogIndexFilter();
		filter.owner(getUser());
		filter.day().orderBy(false);

		DataView<SessionLogIndex> indexView = new DataView<>("indices", FilterDataProvider.of(filter, indexDAO)) {

			private static final long serialVersionUID = 3965261355964888504L;

			@Override
			protected void populateItem(Item<SessionLogIndex> item) {
				item.add(new Label("day", item.getModel().map(SessionLogIndex::getDay)));
				item.add(new IconLink<>("view", item.getModel(), FontAwesome.eye) {
					private static final long serialVersionUID = -4386093805155458459L;

					@Override
					public void onClick() {
						setResponsePage(new SessionLogPage(getModelObject()));
					}
				});
			}
		};
		add(indexView);

		add(new BootstrapPagingNavigator("nav", indexView));

	}

}
