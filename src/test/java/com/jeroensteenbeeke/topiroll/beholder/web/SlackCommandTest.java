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
package com.jeroensteenbeeke.topiroll.beholder.web;

import com.jeroensteenbeeke.hyperion.social.web.filter.SlackCommandFilter;
import com.jeroensteenbeeke.hyperion.solitary.InMemory;
import com.jeroensteenbeeke.topiroll.beholder.frontend.FakeSlackServer;
import com.jeroensteenbeeke.topiroll.beholder.frontend.StartBeholderApplication;
import io.vavr.collection.HashMap;
import io.vavr.control.Option;
import okhttp3.*;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.function.Predicate;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public abstract class SlackCommandTest {
	private static InMemory.Handler handler;

	@BeforeClass
	public static void startApplication() throws Exception {
		handler = StartBeholderApplication
			.createApplicationHandler(new String[0]).orElseThrow(IllegalStateException::new);
		SlackCommandFilter.setRequireCorrectSignature(false);
	}

	@AfterClass
	public static void stopApplication() throws Exception {
		SlackCommandFilter.setRequireCorrectSignature(true);
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
			Request request = new Request.Builder().url("http://127.0.0.1:8081/beholder/slack/command")
												   .header("X-Slack-Signature", "1337")
												   .header("X-Slack-Request-Timestamp", String.valueOf(System.currentTimeMillis()))
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

			assertThat(response, isSuccess());

			Thread.sleep(5000);

			Option<JSONObject> lastCommandResponseReceived = FakeSlackServer.getLastCommandResponseReceived();
			assertTrue(String.format("Last slack response [%s] matches predicate", lastCommandResponseReceived
				.map(JSONAware::toJSONString)
				.getOrElse("NOTHING")), lastCommandResponseReceived
						   .filter(slackResponsePredicate).isDefined());
		}

		private TypeSafeDiagnosingMatcher<Response> isSuccess() {
			return new TypeSafeDiagnosingMatcher<>() {
				@Override
				protected boolean matchesSafely(Response item, Description mismatchDescription) {
					boolean match = item.isSuccessful();

					if (!match) {
						mismatchDescription.appendText("response has status ")
										   .appendValue(item.code())
										   .appendText(" (")
										   .appendText(item.message())
										   .appendText("), with body: ");
						try {
							mismatchDescription
								.appendText(item.body().string());
						} catch (IOException e) {
							mismatchDescription
								.appendText(" <UNREADABLE, EXCEPTION: ")
								.appendText(e.getClass().getSimpleName())
								.appendText(" ")
								.appendValue(e.getMessage());
						}
					}

					return true;
				}

				@Override
				public void describeTo(Description description) {
					description.appendText("response is successful");
				}
			};
		}
	}
}
