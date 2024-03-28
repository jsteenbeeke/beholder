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
package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import com.jeroensteenbeeke.hyperion.heinlein.web.pages.BootstrapBasePage;
import com.jeroensteenbeeke.topiroll.beholder.web.BeholderSession;
import com.jeroensteenbeeke.topiroll.beholder.web.components.LegalPanel;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.OverviewPage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.http.WebResponse;

import jakarta.servlet.http.HttpServletResponse;

public class InternalErrorPage extends BootstrapBasePage {

	private static final long serialVersionUID = 8771238347909522153L;

	public InternalErrorPage() {
		super("Internal error");

		add(new Link<Void>("continue") {
			private static final long serialVersionUID = -153459368902727914L;

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

	@Override
	protected void setHeaders(final WebResponse response)
	{
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}

	@Override
	public boolean isErrorPage() {
		return true;
	}

	@Override
	public boolean isVersioned() {
		return false;
	}
}
