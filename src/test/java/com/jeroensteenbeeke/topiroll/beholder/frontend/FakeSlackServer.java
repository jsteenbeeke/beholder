/**
 * This file is part of Beholder
 * (C) 2016-2019 Jeroen Steenbeeke
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
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
import ratpack.server.RatpackServer;
import ratpack.server.ServerConfig;
import ratpack.util.MultiValueMap;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class FakeSlackServer {
	private static final Logger log = LoggerFactory.getLogger(FakeSlackServer.class);

	private static List<JSONObject> commandResponsesReceived = List.empty();

	private RatpackServer server;

	public FakeSlackServer start() throws Exception {
		this.server = RatpackServer.start(server -> server.serverConfig(ServerConfig.builder().port(5050).build())
														  .handlers(chain -> {
															  chain.get("oauth/authorize", ctx -> {
																  MultiValueMap<String, String> queryParams = ctx
																		  .getRequest()
																		  .getQueryParams();
																  String redirectUri = queryParams
																		  .get("redirect_uri");
																  String state = queryParams.get("state");

																  ctx.redirect(redirectUri + "?code=1337&state=" + state);
															  });
															  chain.post("oauth/access", ctx -> {
																  ctx.getResponse().contentType("application.json");
																  ctx.render("{ \"access_token\": \"I_bet_she_could_succubus\" }");
															  });
															  chain.get("oauth/identity", ctx -> {
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

																  ctx.render(object);
															  });
															  chain.post("command/response", ctx -> {
																  log.info("Slack received command response: ");
																  ctx.getRequest().getBody().next(data -> {
																	  String body = data.getText(StandardCharsets.UTF_8);

																	  log.info(body);

																	  commandResponsesReceived = commandResponsesReceived
																			  .append((JSONObject) new JSONParser().parse(body));

																	  ctx.render("{}");
																  });
															  });
															  chain.all(ctx -> log.info("Slack: {}", ctx
																	  .getRequest()
																	  .getUri()));
														  }));


		int timeout = 0;
		while (!server.isRunning()) {
			Thread.sleep(200L);

			if (timeout++ > 50) {
				throw new TimeoutException();
			}
		}

		log.info("Slack server started");

		return this;
	}

	public static Option<JSONObject> getLastCommandResponseReceived() {
		return commandResponsesReceived.lastOption();
	}

	public FakeSlackServer stop() throws Exception {
		server.stop();
		return this;
	}
}
