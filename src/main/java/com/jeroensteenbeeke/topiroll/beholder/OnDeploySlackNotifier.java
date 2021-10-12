package com.jeroensteenbeeke.topiroll.beholder;

import com.jeroensteenbeeke.hyperion.Hyperion;
import com.jeroensteenbeeke.hyperion.social.api.webhook.WebhookFailure;
import com.jeroensteenbeeke.hyperion.social.api.webhook.WebhookInvocation;
import com.jeroensteenbeeke.hyperion.social.api.webhook.WebhookPayload;
import com.jeroensteenbeeke.hyperion.social.api.webhook.WebhookResponse;
import com.jeroensteenbeeke.hyperion.social.api.webhook.blockkit.Divider;
import com.jeroensteenbeeke.hyperion.social.api.webhook.blockkit.MarkdownText;
import com.jeroensteenbeeke.hyperion.social.api.webhook.blockkit.PlainText;
import com.jeroensteenbeeke.hyperion.social.api.webhook.blockkit.Section;
import com.jeroensteenbeeke.hyperion.solstice.spring.ApplicationMetadataStore;
import com.jeroensteenbeeke.lux.ActionResult;
import com.jeroensteenbeeke.topiroll.beholder.beans.DeployNotificationContext;
import io.vavr.collection.Array;
import io.vavr.control.Option;
import io.vavr.control.Try;
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

	private final DeployNotificationContext deployNotificationContext;

	private final ApplicationMetadataStore metadataStore;

	private final ServletContext servletContext;


	public OnDeploySlackNotifier(DeployNotificationContext deployNotificationContext, ApplicationMetadataStore metadataStore, ServletContext servletContext) {
		this.deployNotificationContext = deployNotificationContext;
		this.metadataStore = metadataStore;
		this.servletContext = servletContext;
	}

	@Override
	public void onAfterInitialized(Application application) {
		final String runningApplicationHash = String.format("%s:%s:%s",
															BeholderApplication.get().getRevision(),
															Hyperion
																	.getRevision()
																	.getOrElse("Unknown"),
															Option
																	.of(System.getenv("DOCKER_IMAGE_ID"))
																	.getOrElse("Unknown")
		);

		Option<String> previousApplicationHash = metadataStore.readString(BeholderApplication.KEY_BEHOLDER_CURRENT_VERSION);

		if (previousApplicationHash.isEmpty() || previousApplicationHash
				.filter(p -> !p.equals(runningApplicationHash)).isDefined()) {
			ActionResult writeResult = metadataStore
					.writeString(BeholderApplication.KEY_BEHOLDER_CURRENT_VERSION, runningApplicationHash);
			writeResult
					.ifOk(() -> {

						String deployWebhook = deployNotificationContext.getDeployWebhook();

						if (deployWebhook == null) {
							return;
						}

						String message = "A new version of Beholder was just deployed to *%s* by *%s*".formatted(deployNotificationContext.getEnvironmentName(), deployNotificationContext.getDeployingInstance());

						Try<WebhookResponse> response = WebhookInvocation
								.invoke(deployWebhook)
								.withPayload(
										createPayload(message)


								);

						response.onSuccess(r -> {
							if (r instanceof WebhookFailure failure) {
								log.error("Could not send deploy notification. Webhook returned {}: {}\n{}", failure.code(), failure.message(), failure.body());
							} else {
								log.info("Deploy notification sent");
							}

						}).onFailure(t -> log.error("Could not send deploy notification", t));
					});
		}
	}

	private WebhookPayload createPayload(String message) {
		WebhookPayload payload = new WebhookPayload(message)
				.withBlock(Section
								   .sectionWithoutHeader(new MarkdownText("*Environment*"))
								   .withField(new PlainText(deployNotificationContext.getEnvironmentName()))
								   .withField(new MarkdownText("*Commit Message*"))
								   .withField(new PlainText(BeholderApplication
																	.get()
																	.getCommitMessage()))
								   .withField(new MarkdownText("*Beholder Revision*"))
								   .withField(new PlainText(BeholderApplication
																	.get()
																	.getRevision()))
								   .withField(new MarkdownText("*Hyperion Revision*"))
								   .withField(new PlainText(Hyperion
																	.getRevision()
																	.getOrElse("unknown")))
				)
				.withBlock(Section
								   .sectionWithoutHeader(new MarkdownText("*Hyperion Commit Message*"))
								   .withField(new PlainText(Hyperion
																	.getCommitTitle()
																	.getOrElse("unknown")))
								   .withField(new MarkdownText("*Docker image ID*"))

								   .withField(new MarkdownText(Option
																	   .of(System.getenv("DOCKER_IMAGE_ID"))
																	   .map(this::asCode)
																	   .getOrElse("unknown")))
								   .withField(new MarkdownText("*JDK*"))
								   .withField(new PlainText(System.getProperty("java.vendor") + " " + Runtime
										   .version()
										   .toString()))
								   .withField(new MarkdownText("*Server*"))
								   .withField(new PlainText(servletContext.getServerInfo()))
				)
				.withBlock(new Divider());

		String commitDetails = BeholderApplication.get().getCommitDetails();

		if (commitDetails != null) {
			payload = payload
					.withBlock(Section
									   .sectionWithoutHeader(new MarkdownText("*Commit details*"))
									   .withField(new PlainText(BeholderApplication
																		.get()
																		.getCommitDetails())));
		}

		return payload;

	}

	private String asCode(String s) {
		return "`" + s + "`";
	}
}
