package com.jeroensteenbeeke.topiroll.beholder.frontend;

import com.jeroensteenbeeke.topiroll.beholder.beans.impl.FakeSlackHandler;
import org.json.simple.JSONObject;
import spark.Service;

import static spark.Service.ignite;

public class FakeSlackServer {

	private final Service service;

	public FakeSlackServer() {
		this.service = ignite().port(FakeSlackHandler.PORT);
	}

	public FakeSlackServer start() {
		// TODO: Implement me!

		// http://localhost:5050/oauth/authorize?response_type=code&client_id=IQNePkalKBYV&redirect_uri=http://localhost:8081/beholder/slack/callback&scope=identity.basic,identity.team,identity.avatar&state=QZ1uKa6blA2csgWESyDe
		service.get("/oauth/authorize", (request, response) -> {
			String redirectUri = request.queryParams("redirect_uri");
			String state = request.queryParams("state");

			response.redirect(redirectUri + "?code=1337&state=" + state);

			return response;
		});

		service.post("/oauth/access", (request, response) -> {
			response.type("application/json");

			return "{ \"access_token\": \"I_bet_she_could_succubus\" }";
		});

		service.get("/oauth/identity", (request, response) -> {
			JSONObject user = new JSONObject();
			user.put("id", "1337");
			user.put("name", System.getProperty("user.name"));
			user.put("image_48", "http://localhost:8081/beholder/img/logo48.png");

			JSONObject team = new JSONObject();
			team.put("id", "31337");
			team.put("name", "Topiroll");

			JSONObject object = new JSONObject();
			object.put("ok", true);
			object.put("user", user);
			object.put("team", team);

			return object.toJSONString();

		});

		service.init();
		service.awaitInitialization();
		return this;
	}

	public FakeSlackServer stop() {
		service.stop();
		return this;
	}
}
