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
package com.jeroensteenbeeke.topiroll.beholder.beans.impl;

import com.github.scribejava.core.oauth.OAuth20Service;
import com.jeroensteenbeeke.hyperion.social.api.SlackAPI;
import com.jeroensteenbeeke.hyperion.util.Randomizer;
import com.jeroensteenbeeke.topiroll.beholder.beans.IdentityService;

import javax.annotation.Nonnull;

public class FakeSlackHandler extends BeholderSlackHandler {
	public static final int PORT = 5050;

	private static final SlackAPI FAKE_SLACK_API = new SlackAPI() {
		@Override
		public String getAccessTokenEndpoint() {
			return String.format("http://localhost:%d/oauth/access", PORT);
		}

		@Override
		protected String getAuthorizationBaseUrl() {
			return String.format("http://localhost:%d/oauth/authorize", PORT);
		}
	};

	public FakeSlackHandler(String applicationBaseUrl, IdentityService identityService) {
		super(applicationBaseUrl, Randomizer.random(12), Randomizer.random(44), identityService);
	}

	@Nonnull
	@Override
	public OAuth20Service createService(String state) {
		return super.createService(state);
	}

	@Override
	protected SlackAPI getSlackAPI() {
		return FAKE_SLACK_API;
	}

	@Override
	protected String getIdentityUrl() {
		return String.format("http://localhost:%d/oauth/identity", PORT);
	}
}
