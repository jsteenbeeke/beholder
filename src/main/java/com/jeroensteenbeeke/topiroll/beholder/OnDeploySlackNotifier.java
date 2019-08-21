package com.jeroensteenbeeke.topiroll.beholder;

import com.jeroensteenbeeke.hyperion.Hyperion;
import com.jeroensteenbeeke.hyperion.solstice.spring.ApplicationMetadataStore;
import com.jeroensteenbeeke.lux.ActionResult;
import com.jeroensteenbeeke.topiroll.beholder.beans.impl.BeholderSlackHandler;
import io.vavr.collection.Array;
import io.vavr.control.Option;
import okhttp3.*;
import org.apache.wicket.Application;
import org.apache.wicket.IApplicationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.io.IOException;

public class OnDeploySlackNotifier implements IApplicationListener {
	private static final Logger log = LoggerFactory.getLogger(OnDeploySlackNotifier.class);
	private static final String DIVIDER = "{\n\t\t\"type\": \"divider\"\n\t}";
	private static final String START_FIELDS = "{\n" +
		"\t\t\"type\": \"section\",\n" +
		"\t\t\"fields\": [";
	private static final String END_FIELDS = "]}";

	private final BeholderSlackHandler slackHandler;

	private final ApplicationMetadataStore metadataStore;

	private final ServletContext servletContext;

	private static final OkHttpClient client = new OkHttpClient();


	public OnDeploySlackNotifier(BeholderSlackHandler slackHandler, ApplicationMetadataStore metadataStore, ServletContext servletContext) {
		this.slackHandler = slackHandler;
		this.metadataStore = metadataStore;
		this.servletContext = servletContext;
	}

	@Override
	public void onAfterInitialized(Application application) {
		final String runningApplicationHash = String.format("%s:%s:%s",
															BeholderApplication.get().getRevision(),
															Hyperion.getRevision().getOrElse("Unknown"),
															Option.of(System.getenv("DOCKER_IMAGE_ID")).getOrElse("Unknown")
		);

		Option<String> previousApplicationHash = metadataStore.readString(BeholderApplication.KEY_BEHOLDER_CURRENT_VERSION);

		if (previousApplicationHash.isEmpty() || previousApplicationHash
			.filter(p -> !p.equals(runningApplicationHash)).isDefined()) {
			ActionResult writeResult = metadataStore
				.writeString(BeholderApplication.KEY_BEHOLDER_CURRENT_VERSION, runningApplicationHash);
			writeResult
				.ifOk(() -> {

					String deployWebhook = slackHandler.getDeployWebhook();
					String message = "A new version of Beholder just deployed";
					String attachments = createAttachments();

					String payload = String.format("{\n\t\"text\": \"%s\",\n\t\"attachments\": [{\"blocks\": [%s]}]\n}", message, attachments);

					if (deployWebhook != null && !deployWebhook.isEmpty()) {
						log.info("Posting payload to Slack: {}", payload);

						Request request = new Request.Builder()
							.post(RequestBody.create(MediaType.parse("application/json"),
													 payload))
							.url(deployWebhook) //post in designated channel
							.build();


						try (Response response = client.newCall(request).execute()) {
							if (!response.isSuccessful()) {
								ResponseBody body = response
									.body();
								log.error("Failed to post deploy message to Slack webhook: {} {}\n{}", response.code(), response
									.message(), body
											  .string());
							}
						} catch (IOException e) {
							log.error(e.getMessage(), e);
						}
					}
				});
		}
	}

	private String createAttachments() {
		StringBuilder sb = new StringBuilder();

		Array<String> fields = Array.empty();

		fields = fields.append(field("mrkdwn", "*Commit Message*"));
		fields = fields.append(field("plain_text", BeholderApplication.get().getCommitMessage()));
		fields = fields.append(field("mrkdwn", "*Beholder Revision*"));
		fields = fields.append(field("plain_text", BeholderApplication.get().getRevision()));
		fields = fields.append(field("mrkdwn", "*Hyperion Revision*"));
		fields = fields.append(field("plain_text", Hyperion.getRevision().getOrElse("unknown")));
		Option<String> hyperionCommitTitle = Hyperion.getCommitTitle();
		if (hyperionCommitTitle.isDefined()) {
			fields = fields.append(field("mrkdwn", "*Hyperion Commit Message*"));
			fields = fields.append(field("plain_text", hyperionCommitTitle.getOrElse("unknown")));
		}

		fields = fields.append(field("mrkdwn", "*Server Info*"));
		fields = fields.append(field("plain_text", servletContext.getServerInfo()));
		fields = fields.append(field("mrkdwn", "*Java Version*"));
		fields = fields.append(field("plain_text", Runtime.version().toString()));

		String dockerImageID = System.getenv("DOCKER_IMAGE_ID");
		if (dockerImageID != null && !dockerImageID.isEmpty()) {
			fields = fields.append(field("mrkdwn", "*Docker Image ID*"));
			fields = fields.append(field("plain_text", System.getenv("DOCKER_IMAGE_ID")));
		}

		sb.append(fields.mkString(START_FIELDS, ",\n\t\t", END_FIELDS));

		String details = BeholderApplication.get().getCommitDetails().replace("\n", "\\n");

		if (!details.isEmpty() && !details.equals("Unknown")) {
			sb.append(",\n\t");
			sb.append(DIVIDER);
			sb.append(",\n\t");

			sb
				.append("{\n" + "\t\t\"type\": \"section\",\n" + "\t\t\"text\": {\n" + "\t\t\t\"type\": \"mrkdwn\",\n" + "\t\t\t\"text\": \"*Commit details*\\n")
				.append(details)
				.append("\"\n")
				.append("\t\t}\n")
				.append("\t}");
		}


		return sb.toString();
	}

	public String field(String type, String text) {
		return "{\n" +
			"\t\t\t\t\"type\": \"" + type + "\",\n" +
			"\t\t\t\t\"text\": \"" + text + "\"\n" +
			"\t\t\t}";
	}

}
