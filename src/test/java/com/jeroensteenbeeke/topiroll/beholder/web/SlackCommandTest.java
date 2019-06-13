package com.jeroensteenbeeke.topiroll.beholder.web;

import com.jeroensteenbeeke.hyperion.solitary.InMemory;
import com.jeroensteenbeeke.topiroll.beholder.frontend.FakeSlackServer;
import com.jeroensteenbeeke.topiroll.beholder.frontend.StartBeholderApplication;
import io.vavr.collection.HashMap;
import io.vavr.control.Option;
import okhttp3.*;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.function.Predicate;

import static org.junit.Assert.assertTrue;

public abstract class SlackCommandTest {
	private static InMemory.Handler handler;

	@BeforeClass
	public static void startApplication() throws Exception {
		handler = StartBeholderApplication
			.createApplicationHandler(new String[0]).orElseThrow(IllegalStateException::new);
	}

	@AfterClass
	public static void stopApplication() throws Exception {
		handler.terminate();
	}

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
			System.out.println("Building request");
			Request request = new Request.Builder().url("http://127.0.0.1:8081/beholder/slack/command")
												   .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"),
																			"command=" + URLEncoder.encode(command, Charset
																				.forName("UTF-8")) +
																				"&response_url=" + URLEncoder.encode("http://127.0.0.1:5050/command/response", Charset
																				.forName("UTF-8")) +
																				params
																					.map(t -> String.format("%s=%s", t._1, URLEncoder
																						.encode(t._2, Charset.forName("UTF-8"))))
																					.mkString("&", "&", "")
												   )).build();

			Response response = new OkHttpClient().newCall(request).execute();

			assertTrue(response.isSuccessful());

			Thread.sleep(1000);

			Option<JSONObject> lastCommandResponseReceived = FakeSlackServer.getLastCommandResponseReceived();
			assertTrue(String.format("Last slack response [%s] matches predicate", lastCommandResponseReceived
				.map(JSONAware::toJSONString)
				.getOrElse("NOTHING")), lastCommandResponseReceived
						   .filter(slackResponsePredicate).isDefined());
		}
	}
}
