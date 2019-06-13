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
package com.jeroensteenbeeke.topiroll.beholder.frontend;

import com.jeroensteenbeeke.topiroll.beholder.beans.impl.FakeSlackHandler;
import io.vavr.collection.List;
import io.vavr.control.Option;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Service;

import static spark.Service.ignite;

public class FakeSlackServer {
	private static final Logger log = LoggerFactory.getLogger(FakeSlackServer.class);

	private static List<JSONObject> commandResponsesReceived = List.empty();

	private final Service service;

	public FakeSlackServer() {
		this.service = ignite().port(FakeSlackHandler.PORT);
	}

	public FakeSlackServer start() {
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

		service.post("/command/response", (request, response) -> {
			log.info("Slack received command response: ");
			String body = request.body();

			log.info(body);

			commandResponsesReceived = commandResponsesReceived.append((JSONObject) new JSONParser().parse(body));

			return "{}";
		});

		service.after((request, response) -> log.info("Slack: {}", request.url()));

		service.init();
		service.awaitInitialization();

		log.info("Slack server started");

		return this;
	}

	public static Option<JSONObject> getLastCommandResponseReceived() {
		return commandResponsesReceived.lastOption();
	}

	public FakeSlackServer stop() {
		service.stop();
		return this;
	}
}
