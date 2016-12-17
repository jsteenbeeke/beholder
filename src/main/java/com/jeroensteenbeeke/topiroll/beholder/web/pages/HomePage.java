/**
 * This file is part of Beholder
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import org.apache.wicket.RestartResponseAtInterceptPageException;

import com.jeroensteenbeeke.hyperion.heinlein.web.pages.BootstrapBasePage;
import com.jeroensteenbeeke.hyperion.social.web.components.slack.SlackLink;
import com.jeroensteenbeeke.topiroll.beholder.web.BeholderSession;

public class HomePage extends BootstrapBasePage {
	private static final long serialVersionUID = 1L;

	public HomePage() {
		super("Beholder");

		if (BeholderSession.get().getUser() != null) {
			throw new RestartResponseAtInterceptPageException(
					new OverviewPage());
		}

		add(new SlackLink("slack"));
	}

}