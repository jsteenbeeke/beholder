package com.jeroensteenbeeke.topiroll.beholder.web;

import com.jeroensteenbeeke.topiroll.beholder.frontend.FakeSlackServer;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.AbstractPageTest;
import io.vavr.collection.HashMap;
import okhttp3.*;
import org.json.simple.JSONObject;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.function.Predicate;

import static org.junit.Assert.assertTrue;

public abstract class SlackCommandTest extends AbstractPageTest {
	public CommandBuilder issueCommand(@Nonnull String command) {
		assertTrue(command.startsWith("/"));

		return new CommandBuilder(command);
	}

	protected static class CommandBuilder {
		private final String command;

		private final HashMap<String, String> params;

		private CommandBuilder(String command) {
			this(command, HashMap.empty());
		}

		private CommandBuilder(String command, HashMap<String, String> params) {
			this.command = command;
			this.params = params;
		}

		public CommandBuilder withParam(String key, String value) {
			return new CommandBuilder(command, params.put(key, value));
		}

		public void expectingMessage(String message) throws IOException, InterruptedException {
			expectingResponse(o -> o.get("text").equals(message));
		}

		public void expectingResponse(Predicate<JSONObject> slackResponsePredicate) throws IOException, InterruptedException {
			Request request = new Request.Builder().url("http://localhost:8081/beholder/slack/command")
												 .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"),
																		  "command=" + URLEncoder.encode(command, Charset
																			  .forName("UTF-8")) +
																			  "&response_url=" + URLEncoder.encode("http://localhost:5050/command/response", Charset
																			  .forName("UTF-8")) +
																			  params
																				  .map(t -> String.format("%s=%s", t._1, URLEncoder
																					  .encode(t._2, Charset.forName("UTF-8"))))
																				  .mkString("&", "&", "")
												 )).build();

			Response response = new OkHttpClient().newCall(request).execute();

			assertTrue(response.isSuccessful());

			Thread.sleep(1000);

			assertTrue("Slack response matches predicate", FakeSlackServer.getLastCommandResponseReceived().filter(slackResponsePredicate).isDefined());
		}
	}
}
