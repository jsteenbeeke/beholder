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
package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster;

import javax.annotation.CheckForNull;

import com.jeroensteenbeeke.topiroll.beholder.web.pages.HomePage;
import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.markup.html.WebMarkupContainer;

import com.jeroensteenbeeke.hyperion.heinlein.web.pages.BootstrapBasePage;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.EntityPageInitializer;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.web.BeholderSession;
import com.jeroensteenbeeke.topiroll.beholder.web.components.BeholderNavBar;
import com.jeroensteenbeeke.topiroll.beholder.web.components.LegalPanel;

public abstract class AuthenticatedPage extends BootstrapBasePage
		implements EntityPageInitializer {
	private static final long serialVersionUID = 1L;

	public AuthenticatedPage(String title) {
		super(title);

		add(new BeholderNavBar("navbar"));

		if (getUser() == null) {
			BeholderSession.get().invalidate();
			throw new RestartResponseAtInterceptPageException(HomePage.class);
		}
		
		add(new LegalPanel("legal"));
	}

	@CheckForNull
	public BeholderUser getUser() {
		return BeholderSession.get().getUser();
	}

	@Override
	public Component createNavComponent(String id) {
		return new WebMarkupContainer(id).setVisible(false);
	}
}