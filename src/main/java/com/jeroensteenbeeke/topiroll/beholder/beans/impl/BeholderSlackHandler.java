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

import com.jeroensteenbeeke.topiroll.beholder.beans.WebHookSupplier;
import io.vavr.control.Option;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.json.simple.JSONObject;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.jeroensteenbeeke.hyperion.social.beans.slack.SlackHandler;
import com.jeroensteenbeeke.lux.TypedResult;
import com.jeroensteenbeeke.topiroll.beholder.beans.IdentityService;
import com.jeroensteenbeeke.topiroll.beholder.beans.data.UserDescriptor;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.web.BeholderSession;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.OverviewPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.SlackErrorPage;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public class BeholderSlackHandler extends SlackHandler implements
		WebHookSupplier {
	private String applicationBaseUrl;

	private String clientId;

	private String clientSecret;

	private String signingSecret;

	private String deployWebhook;

	private IdentityService identityService;

	public BeholderSlackHandler(String applicationBaseUrl, String clientId, String clientSecret, String signingSecret, String deployWebhook, IdentityService identityService) {
		this.applicationBaseUrl = applicationBaseUrl;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.signingSecret = signingSecret;
		this.deployWebhook = deployWebhook;
		this.identityService = identityService;
	}

	@Nonnull
	@Override
	public String getClientId() {
		return clientId;
	}

	@Nonnull
	@Override
	public String getClientSecret() {
		return clientSecret;
	}

	@Nonnull
	public String getSigningSecret() {
		return signingSecret;
	}

	@CheckForNull
	@Override
	public String getDeployWebhook() {
		return deployWebhook;
	}

	@Nonnull
	@Override
	public String getApplicationBaseUrl() {
		return applicationBaseUrl;
	}

	@Override
	public void onError(@Nonnull String message) {
		throw new RestartResponseAtInterceptPageException(
				new SlackErrorPage(message));

	}

	@Nonnull
	@Override
	public String getScopes() {
		return "identity.basic,identity.team,identity.avatar";
	}

	@Override
	public void onAccessTokenReceived(@Nonnull OAuth20Service service,
									  @Nonnull OAuth2AccessToken accessToken) {
		String tokenString = accessToken.getAccessToken();

		TypedResult<JSONObject> result = getUserInfo(service,
				accessToken);

		if (result.isOk()) {
			JSONObject response = result.getObject();

			Boolean ok = (Boolean) response.get("ok");

			if (ok == null || !ok) {
				onError("Slack returned non-OK response status: "
						.concat(response.toJSONString()));
			} else {
				JSONObject user = (JSONObject) response.get("user");
				JSONObject team = (JSONObject) response.get("team");

				if (user == null || team == null) {
					onError("Slack returned incomplete JSON response: "
							.concat(response.toJSONString()));
				} else {
					String userId = (String) user.get("id");
					String teamId = (String) team.get("id");

					String userName = (String) user.get("name");
					String teamName = (String) team.get("name");
					String avatar = (String) user.get("image_48");

					BeholderUser beholderUser = identityService.getOrCreateUser(
							new UserDescriptor().setAccessToken(tokenString)
									.setAvatar(avatar).setTeamId(teamId)
									.setTeamName(teamName).setUserId(userId)
									.setUserName(userName));

					BeholderSession.get().setUser(beholderUser);

					throw new RestartResponseAtInterceptPageException(
							OverviewPage.class);
				}
			}

		} else {
			onError(result.getMessage());
		}

	}

	@Override
	public Option<String> getUserState() {
		return Option.of(BeholderSession.get().getState());
	}

	@Override
	public void setUserState(@Nonnull String state) {
		BeholderSession.get().setState(state);

	}

}
