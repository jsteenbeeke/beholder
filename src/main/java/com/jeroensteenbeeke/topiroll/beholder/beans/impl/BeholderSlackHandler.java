package com.jeroensteenbeeke.topiroll.beholder.beans.impl;

import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.jeroensteenbeeke.hyperion.social.beans.slack.SlackHandler;
import com.jeroensteenbeeke.hyperion.util.TypedActionResult;
import com.jeroensteenbeeke.topiroll.beholder.beans.IdentityService;
import com.jeroensteenbeeke.topiroll.beholder.beans.IdentityService.UserDescriptor;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.web.BeholderSession;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.OverviewPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.SlackErrorPage;

@Component
public class BeholderSlackHandler extends SlackHandler {
	@Value("${application.baseurl}")
	private String applicationBaseUrl;

	@Value("${slack.clientid}")
	private String clientId;

	@Value("${slack.clientsecret}")
	private String clientSecret;

	@Autowired
	private IdentityService identityService;

	@Override
	public String getClientId() {
		return clientId;
	}

	@Override
	public String getClientSecret() {
		return clientSecret;
	}

	@Override
	public String getApplicationBaseUrl() {
		return applicationBaseUrl;
	}

	@Override
	public void onError(String message) {
		throw new RestartResponseAtInterceptPageException(
				new SlackErrorPage(message));

	}

	@Override
	public String getScopes() {
		return "identity.basic,identity.team,identity.avatar";
	}

	@Override
	public void onAccessTokenReceived(OAuth20Service service,
			OAuth2AccessToken accessToken) {
		String tokenString = accessToken.getAccessToken();

		TypedActionResult<JSONObject> result = getUserInfo(service,
				accessToken);

		if (result.isOk()) {
			JSONObject response = result.getObject();

			Boolean ok = (Boolean) response.get("ok");

			if (ok == null || !ok.booleanValue()) {
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
	public String getUserState() {
		return BeholderSession.get().getState();
	}

	@Override
	public void setUserState(String state) {
		BeholderSession.get().setState(state);

	}

}
