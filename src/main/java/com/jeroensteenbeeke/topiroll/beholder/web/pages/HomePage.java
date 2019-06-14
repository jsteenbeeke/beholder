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
package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import com.jeroensteenbeeke.hyperion.heinlein.web.pages.BootstrapBasePage;
import com.jeroensteenbeeke.hyperion.social.web.components.slack.SlackLink;
import com.jeroensteenbeeke.topiroll.beholder.web.BeholderSession;
import com.jeroensteenbeeke.topiroll.beholder.web.components.LegalPanel;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.OverviewPage;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.MetaDataHeaderItem;
import org.apache.wicket.request.UrlUtils;

public class HomePage extends BootstrapBasePage {
	private static final long serialVersionUID = 1L;

	public HomePage() {
		super("Beholder");

		if (BeholderSession.get().getUser() != null) {
			throw new RestartResponseAtInterceptPageException(
					new OverviewPage());
		}

		add(new SlackLink("slack"));
		
		add(new LegalPanel("legal"));
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		response.render(MetaDataHeaderItem.forLinkTag("icon", UrlUtils.rewriteToContextRelative("img/favicon/favicon-32x32.png", getRequestCycle())));
		response.render(MetaDataHeaderItem.forLinkTag("icon", UrlUtils.rewriteToContextRelative("img/favicon/favicon-16x16.png", getRequestCycle())));
	}
}
