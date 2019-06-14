package com.jeroensteenbeeke.topiroll.beholder;

import com.jeroensteenbeeke.topiroll.beholder.beans.impl.BeholderSlackHandler;
import com.jeroensteenbeeke.topiroll.beholder.beans.impl.FakeSlackHandler;
import okhttp3.*;
import org.apache.wicket.Application;
import org.apache.wicket.IApplicationListener;

import java.io.IOException;

public class OnDeploySlackNotifier implements IApplicationListener {
	private final BeholderSlackHandler slackHandler;
	private static final OkHttpClient client = new OkHttpClient();


	public OnDeploySlackNotifier(BeholderSlackHandler slackHandler) {
		this.slackHandler = slackHandler;
	}

	@Override
	public void onAfterInitialized(Application application) {
		if (!(slackHandler instanceof FakeSlackHandler)) {
			String message = String.format("A new version of Beholder just deployed, revision %s", BeholderApplication.get().getRevision());
			Request request = new Request.Builder()
				.post(RequestBody.create(MediaType.parse("application/json"), "{\n" +
						String.format("\t\"text\": \"%s\",\n", message.replace("\"", "\\\",")) +
						"}"))
				.url("https://hooks.slack.com/services/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") //post in #beholder
				.build();


			Response response = null;
			try {
				response = client.newCall(request).execute();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
