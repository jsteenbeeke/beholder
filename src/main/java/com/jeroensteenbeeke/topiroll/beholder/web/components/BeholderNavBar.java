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
package com.jeroensteenbeeke.topiroll.beholder.web.components;

import com.jeroensteenbeeke.topiroll.beholder.web.BeholderSession;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.HomePage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.OverviewPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.PrepareSessionPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.RunSessionPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.SessionLogOverviewPage;
import org.apache.wicket.markup.html.link.Link;

import org.jetbrains.annotations.NotNull;

public class BeholderNavBar extends org.apache.wicket.markup.html.panel.Panel {

	private static final long serialVersionUID = 1L;


	public BeholderNavBar(@NotNull String id) {
		super(id);

		Link<Void> brandLink = new Link<Void>("brand") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {

				setResponsePage(new OverviewPage());
			}

		};

		brandLink.add(new UserImage("image"));

		add(brandLink);

		add(new Link<Void>("prepare") {
			private static final long serialVersionUID = 7525798483831866821L;

			@Override
			public void onClick() {
				setResponsePage(new PrepareSessionPage());
			}
		});

		add(new Link<Void>("run") {
			private static final long serialVersionUID = -1332593238915006341L;

			@Override
			public void onClick() {
				setResponsePage(new RunSessionPage());
			}
		});

		add(new Link<Void>("log") {
			private static final long serialVersionUID = -1332593238915006341L;

			@Override
			public void onClick() {
				setResponsePage(new SessionLogOverviewPage());
			}
		});


		add(new Link<Void>("logout") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				BeholderSession.get().invalidate();
				setResponsePage(HomePage.class);
			}

		});


	}

}
