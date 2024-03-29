/*
 * This file is part of Beholder
 * Copyright (C) 2016 - 2023 Jeroen Steenbeeke
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
package com.jeroensteenbeeke.topiroll.beholder;

import com.jeroensteenbeeke.hyperion.Hyperion;
import com.jeroensteenbeeke.hyperion.social.api.webhook.WebhookFailure;
import com.jeroensteenbeeke.hyperion.social.api.webhook.WebhookInvocation;
import com.jeroensteenbeeke.hyperion.social.api.webhook.WebhookPayload;
import com.jeroensteenbeeke.hyperion.social.api.webhook.WebhookResponse;
import com.jeroensteenbeeke.hyperion.social.api.webhook.blockkit.Divider;
import com.jeroensteenbeeke.hyperion.social.api.webhook.blockkit.MarkdownText;
import com.jeroensteenbeeke.hyperion.social.api.webhook.blockkit.PlainText;
import com.jeroensteenbeeke.hyperion.social.api.webhook.blockkit.TextSection;
import com.jeroensteenbeeke.hyperion.social.api.webhook.blockkit.FieldsSection;
import com.jeroensteenbeeke.hyperion.solstice.spring.ApplicationMetadataStore;
import com.jeroensteenbeeke.lux.ActionResult;
import com.jeroensteenbeeke.topiroll.beholder.beans.DeployNotificationContext;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.apache.wicket.Application;
import org.apache.wicket.IApplicationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletContext;

public class OnDeploySlackNotifier implements IApplicationListener {
	private static final Logger log = LoggerFactory.getLogger(OnDeploySlackNotifier.class);

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

						WebhookPayload payload = createPayload(message);

						log.info("Sending notification: {}", payload.toJson());

						Try<WebhookResponse> response = WebhookInvocation
								.invoke(deployWebhook)
								.withPayload(payload);

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
				.withBlock(new TextSection(new MarkdownText(message)))
				.withBlock(FieldsSection
								   .create(new MarkdownText("*Environment*"))
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
				.withBlock(FieldsSection
								   .create(new MarkdownText("*Hyperion Commit Message*"))
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
				);

		String commitDetails = BeholderApplication.get().getCommitDetails();

		if (commitDetails != null && !commitDetails.isBlank()) {
			payload = payload
					.withBlock(FieldsSection
									   .create(new MarkdownText("*Commit details*"))
									   .withField(new PlainText(commitDetails)));
		}

		payload = payload.withBlock(new Divider());

		return payload;

	}

	private String asCode(String s) {
		return "`" + s + "`";
	}
}
