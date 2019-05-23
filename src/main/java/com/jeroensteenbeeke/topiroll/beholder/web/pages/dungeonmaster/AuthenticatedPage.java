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

import com.jeroensteenbeeke.hyperion.heinlein.web.pages.BootstrapBasePage;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.entity.EntityFormSupport;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.web.BeholderSession;
import com.jeroensteenbeeke.topiroll.beholder.web.components.BeholderNavBar;
import com.jeroensteenbeeke.topiroll.beholder.web.components.LegalPanel;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.HomePage;
import io.vavr.control.Option;
import org.apache.wicket.RestartResponseAtInterceptPageException;

import javax.annotation.CheckForNull;

public abstract class AuthenticatedPage extends BootstrapBasePage
		implements EntityFormSupport {
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

	public Option<BeholderUser> user() {
		return Option.of(getUser());
	}
}
