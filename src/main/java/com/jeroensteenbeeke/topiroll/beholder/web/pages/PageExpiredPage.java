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
package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import com.jeroensteenbeeke.hyperion.heinlein.web.pages.BootstrapBasePage;
import com.jeroensteenbeeke.hyperion.rollbar.RollBarReference;
import com.jeroensteenbeeke.topiroll.beholder.web.BeholderSession;
import com.jeroensteenbeeke.topiroll.beholder.web.components.LegalPanel;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.OverviewPage;
import org.apache.wicket.markup.html.link.Link;

public class PageExpiredPage extends BootstrapBasePage {
	private static final long serialVersionUID = 1653313737281856944L;

	public PageExpiredPage() {
		super("Page expired");

		add(new Link<Void>("continue") {
			private static final long serialVersionUID = -4236878142549881879L;

			@Override
			public void onClick() {
				if (BeholderSession.get().getUser() != null) {
					setResponsePage(OverviewPage.class);
				} else {
					setResponsePage(HomePage.class);
				}
			}
		});

		add(new LegalPanel("legal"));

	}
}
