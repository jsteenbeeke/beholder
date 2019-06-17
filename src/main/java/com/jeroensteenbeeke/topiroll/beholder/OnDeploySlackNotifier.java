package com.jeroensteenbeeke.topiroll.beholder;

import com.jeroensteenbeeke.hyperion.Hyperion;
import com.jeroensteenbeeke.topiroll.beholder.beans.impl.BeholderSlackHandler;
import com.jeroensteenbeeke.topiroll.beholder.beans.impl.FakeSlackHandler;
import io.vavr.collection.Array;
import okhttp3.*;
import org.apache.wicket.Application;
import org.apache.wicket.IApplicationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.stream.Stream;

public class OnDeploySlackNotifier implements IApplicationListener {
	private static final Logger log = LoggerFactory.getLogger(OnDeploySlackNotifier.class);
	private static final String DIVIDER = "{\n\t\t\"type\": \"divider\"\n\t}";
	private static final String START_FIELDS = "{\n" +
		"\t\t\"type\": \"section\",\n" +
		"\t\t\"fields\": [";
	private static final String END_FIELDS = "]}";

	private final BeholderSlackHandler slackHandler;
	private static final OkHttpClient client = new OkHttpClient();


	public OnDeploySlackNotifier(BeholderSlackHandler slackHandler) {
		this.slackHandler = slackHandler;
	}

	@Override
	public void onAfterInitialized(Application application) {
		String deployWebhook = slackHandler.getDeployWebhook();
		String message = "A new version of Beholder just deployed";
		String attachments = createAttachments();

		String payload = String.format("{\n\t\"text\": \"%s\",\n\t\"attachments\": [%s]\n}", message, attachments);

		if (deployWebhook != null && !deployWebhook.isEmpty()) {

			Request request = new Request.Builder()
				.post(RequestBody.create(MediaType.parse("application/json"),
										 payload))
				.url(deployWebhook) //post in designated channel
				.build();


			Response response;
			try {
				response = client.newCall(request).execute();

				if (!response.isSuccessful()) {
					log.error("Failed to post deploy message to Slack webhook: {} {}\n{}", response.code(), response.message(), response
						.body()
						.string());
				}
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	private String createAttachments() {
		StringBuilder sb = new StringBuilder();
		sb.append(DIVIDER);

		Array<String> fields = Array.empty();

		fields = fields.append(field("plain_text", "Commit Message", false));
		fields = fields.append(field("plain_text", BeholderApplication.get().getCommitMessage(), false));
		fields = fields.append(field("plain_text", "Beholder Revision", false));
		fields = fields.append(field("plain_text", BeholderApplication.get().getRevision(), false));
		fields = fields.append(field("plain_text", "Hyperion Revision", false));
		fields = fields.append(field("plain_text", Hyperion.getRevision().getOrElse("unknown"), false));

		if (System.getenv("DOCKER_IMAGE_ID") != null) {
			fields = fields.append(field("plain_text", "Docker Image ID", false));
			fields = fields.append(field("plain_text", System.getenv("DOCKER_IMAGE_ID"), false));
		}

		sb.append(",\n\t");
		sb.append(fields.mkString(START_FIELDS, ",\n\t\t", END_FIELDS));
		sb.append(",\n\t");
		sb.append(DIVIDER);
		sb.append(",\n\t");

		sb
			.append("{\n" + "\t\t\"type\": \"section\",\n" + "\t\t\"text\": {\n" + "\t\t\t\"type\": \"mrkdwn\",\n" + "\t\t\t\"text\": \"**Commit details**\\n")
			.append(BeholderApplication.get().getCommitDetails().replace("\n", "\\n"))
			.append("\"\n")
			.append("\t\t}\n")
			.append("\t}");


		return sb.toString();
	}

	public String field(String type, String text, boolean emoji) {
		return "{\n" +
			"\t\t\t\t\"type\": \"" + type + "\",\n" +
			"\t\t\t\t\"text\": \"" + text + "\",\n" +
			"\t\t\t\t\"emoji\": " + emoji + "\n" +
			"\t\t\t}";
	}

}
