package com.jeroensteenbeeke.topiroll.beholder;

import com.jeroensteenbeeke.hyperion.Hyperion;
import com.jeroensteenbeeke.topiroll.beholder.beans.impl.BeholderSlackHandler;
import com.jeroensteenbeeke.topiroll.beholder.beans.impl.FakeSlackHandler;
import okhttp3.*;
import org.apache.wicket.Application;
import org.apache.wicket.IApplicationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class OnDeploySlackNotifier implements IApplicationListener {
	private static final Logger log = LoggerFactory.getLogger(OnDeploySlackNotifier.class);

	private final BeholderSlackHandler slackHandler;
	private static final OkHttpClient client = new OkHttpClient();


	public OnDeploySlackNotifier(BeholderSlackHandler slackHandler) {
		this.slackHandler = slackHandler;
	}

	@Override
	public void onAfterInitialized(Application application) {
		String deployWebhook = slackHandler.getDeployWebhook();
		if (deployWebhook != null && !deployWebhook.isEmpty()) {
			String message = String.format("A new version of Beholder just deployed, revision %s (Hyperion %s)", BeholderApplication.get().getRevision(), Hyperion.getRevision());
			Request request = new Request.Builder()
				.post(RequestBody.create(MediaType.parse("application/json"), "{\n" +
						String.format("\t\"text\": \"%s\",\n", message.replace("\"", "\\\",")) +
						"}"))
				.url(slackHandler.getDeployWebhook()) //post in designated channel
				.build();


			Response response;
			try {
				response = client.newCall(request).execute();

				if (!response.isSuccessful()) {
					log.error("Failed to post deploy message to Slack webhook: {} {}\n{}", response.code(), response.message(), response.body().string());
				}
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}
	}
}
