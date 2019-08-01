/**
 * This file is part of Beholder
 * (C) 2016-2019 Jeroen Steenbeeke
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
package com.jeroensteenbeeke.topiroll.beholder.frontend;

import com.jeroensteenbeeke.hyperion.solitary.InMemory;
import com.jeroensteenbeeke.hyperion.solitary.InMemory.Handler;
import com.jeroensteenbeeke.imagesrv.ImageServer;
import org.apache.commons.cli.*;
import org.apache.wicket.protocol.ws.javax.WicketServerEndpointConfig;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

import javax.servlet.ServletException;
import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;

public class StartBeholderApplication {


	public static void main(String[] args) throws Exception {
		createApplicationHandler(args).ifPresent(Handler::waitForKeyPress);
	}

	public static Optional<Handler> createApplicationHandler(String[] args)
		throws Exception {
		SerializableConsumer<WebAppContext> initWebsockets = context -> {
			try {
				ServerContainer wscontainer = WebSocketServerContainerInitializer
					.configureContext(context);

				wscontainer.addEndpoint(new WicketServerEndpointConfig());
			} catch (DeploymentException | ServletException e) {
				e.printStackTrace();
			}
		};

		Options options = new Options();

		options.addOption(null, Arguments.ENABLE_POSTGRES_DB, false, "Use Postgres in Docker instead of H2");

		options.addOption(null, Arguments.SLACK_CLIENT_ID, true, "Slack client ID");
		options.addOption(null, Arguments.SLACK_CLIENT_SECRET, true, "Slack client secret");
		options.addOption(null, Arguments.SLACK_SIGNING_SECRET, true, "Slack signing secret");

		options.addOption(null, Arguments.AMAZON_CLIENT_ID, true, "Amazon client ID");
		options.addOption(null, Arguments.AMAZON_CLIENT_SECRET, true, "Amazon client secret");
		options.addOption(null, Arguments.AMAZON_BUCKET, true, "Amazon bucket name");
		options.addOption(null, Arguments.AMAZON_URL_PREFIX, true, "Cloudfront URL prefix, used to prefix image URLs");
		options.addOption(null, Arguments.AMAZON_REGION, true, "Amazon region");
		options.addOption(null, Arguments.ROLLBAR_CLIENT_ID, true, "Rollbar client ID");
		options.addOption(null, Arguments.ROLLBAR_CLIENT_SECRET, true, "Rollbar client secret");
		options.addOption(null, Arguments.ROLLBAR_ENVIRONMENT, true, "Rollbar environment");
		options.addOption(Arguments.HELP_SHORT, Arguments.HELP_LONG, false, "Display usage info");

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options, args);

		if (cmd.hasOption(Arguments.HELP_LONG) || cmd.hasOption(Arguments.HELP_LONG)) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("StartBeholderApplication", options);
			System.exit(0);
		}

		boolean slackEnabled = cmd.hasOption(Arguments.SLACK_CLIENT_ID) && cmd.hasOption(Arguments.SLACK_CLIENT_SECRET);
		boolean amazonEnabled = cmd.hasOption(Arguments.AMAZON_CLIENT_ID) && cmd.hasOption(Arguments.AMAZON_CLIENT_SECRET) && cmd
			.hasOption(Arguments.AMAZON_BUCKET) && cmd.hasOption(Arguments.AMAZON_URL_PREFIX);
		boolean rollbarEnabled = cmd.hasOption(Arguments.ROLLBAR_CLIENT_ID) && cmd.hasOption(Arguments.ROLLBAR_CLIENT_SECRET) && cmd
			.hasOption(Arguments.ROLLBAR_ENVIRONMENT);

		InMemory.InMemoryFinalizer finalizer = InMemory.run("beholder-web").withContextPath("/beholder/")
													   .withContextConsumer(initWebsockets);


		if (!slackEnabled) {
			System.out
				.printf("Slack login disabled, please specify arguments --%1$s and --%2$s to enable", Arguments.SLACK_CLIENT_ID, Arguments.SLACK_CLIENT_SECRET)
				.println();

			System.setProperty("slack.login.disabled", "true");

			final FakeSlackServer localSlackServer = new FakeSlackServer();

			finalizer
				.withStartListener(server -> {
					localSlackServer.start();
					System.out.println("===================================================");
					System.out.println("===================================================");
					System.out.println("===          FAKE SLACK SERVER ACTIVE           ===");
					System.out.println("===                                             ===");
					System.out.println("=== If you want to log in using the real Slack, ===");
					System.out.println("=== please start the application with the       ===");
					System.out.println("=== following options:                          ===");
					System.out.println("===                                             ===");
					System.out.println("===    --slack-client-id                        ===");
					System.out.println("===    --slack-client-secret                    ===");
					System.out.println("===    --slack-signing-secret                   ===");
					System.out.println("===                                             ===");
					System.out.println("===================================================");
					System.out.println("===================================================");
				})
				.withStopListener(server -> localSlackServer.stop())
				.withProperty("slack.signingsecret", "90ac37f5bb5617abb86e8f46f503c46d5010abd188e5c3fe58b4d9bde21b08ca");

		} else {
			finalizer.withProperty("slack.clientid", cmd.getOptionValue(Arguments.SLACK_CLIENT_ID))
					 .withProperty("slack.clientsecret", cmd.getOptionValue(Arguments.SLACK_CLIENT_SECRET))
					 .withProperty("slack.signingsecret", cmd.getOptionValue(Arguments.SLACK_SIGNING_SECRET));
		}

		if (!amazonEnabled) {
			System.out
				.printf("Amazon S3 image storage disabled, please specify arguments --%1$s, --%2$s, --%3$s and --%4$s to enable",
						Arguments.AMAZON_CLIENT_ID, Arguments.AMAZON_CLIENT_SECRET, Arguments.AMAZON_BUCKET, Arguments.AMAZON_URL_PREFIX)
				.println();
			finalizer.withProperty("remote.image.url.prefix", "http://localhost:4040/images/");

			System.setProperty("amazon.images.disabled", "true");

			final ImageServer localImageServer = new ImageServer(4040);

			finalizer.withStartListener(server -> {
				try {
					localImageServer.start();

					System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
					System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
					System.out.println("%%%          MOCK IMAGE SERVER ACTIVE           %%%");
					System.out.println("%%%                                             %%%");
					System.out.println("%%% If you want to use Amazon S3 for images,    %%%");
					System.out.println("%%% please start the application with the       %%%");
					System.out.println("%%% following options:                          %%%");
					System.out.println("%%%                                             %%%");
					System.out.println("%%%    --amazon-client-id                       %%%");
					System.out.println("%%%    --amazon-client-secret                   %%%");
					System.out.println("%%%    --amazon-region                          %%%");
					System.out.println("%%%    --amazon-bucket                          %%%");
					System.out.println("%%%    --amazon-url-prefix                      %%%");
					System.out.println("%%%                                             %%%");
					System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
					System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
				} catch (IOException ioe) {
					throw new RuntimeException(ioe);
				}
			}).withStopListener(server -> localImageServer.stop());
		} else {
			finalizer.withProperty("amazon.clientid", cmd.getOptionValue(Arguments.AMAZON_CLIENT_ID))
					 .withProperty("amazon.clientsecret", cmd.getOptionValue(Arguments.AMAZON_CLIENT_SECRET))
					 .withProperty("amazon.bucketname", cmd.getOptionValue(Arguments.AMAZON_BUCKET))
					 .withProperty("remote.image.url.prefix", cmd.getOptionValue(Arguments.AMAZON_URL_PREFIX));
			if (cmd.hasOption(Arguments.AMAZON_REGION)) {
				finalizer.withProperty("amazon.region", cmd.getOptionValue(Arguments.AMAZON_REGION));
			} else {
				finalizer.withProperty("amazon.region", "eu-central-1");
			}
		}

		if (cmd.hasOption(Arguments.ENABLE_POSTGRES_DB)) {
			finalizer.withDockerizedPostgres();
		}

		if (!rollbarEnabled) {
			System.out
				.printf("Rollbar error logging disabled, please specify arguments --%1$s, --%2$s and --%3$s to enable",
						Arguments.ROLLBAR_CLIENT_ID, Arguments.ROLLBAR_CLIENT_SECRET, Arguments.ROLLBAR_ENVIRONMENT)
				.println();

		} else {
			finalizer.withProperty("rollbar.server.apiKey", args[6])
					 .withProperty("rollbar.client.apiKey", args[7])
					 .withProperty("rollbar.environment", args[8]);
		}

		return finalizer.withProperty("application.baseurl",
									  "http://localhost:8081/beholder/")
						.withProperty("application.sourceurl",
									  "file://" + System.getProperty("user.dir"))
						.withoutShowingSql()
						.atPort(8081);
	}

	public static class Arguments {
		static final String SLACK_CLIENT_ID = "slack-client-id";
		static final String SLACK_CLIENT_SECRET = "slack-client-secret";
		static final String SLACK_SIGNING_SECRET = "slack-signing-secret";
		static final String AMAZON_CLIENT_ID = "amazon-client-id";
		static final String AMAZON_CLIENT_SECRET = "amazon-client-secret";
		static final String AMAZON_BUCKET = "amazon-bucket";
		static final String AMAZON_URL_PREFIX = "amazon-url-prefix";
		static final String AMAZON_REGION = "amazon-region";
		static final String ROLLBAR_CLIENT_ID = "rollbar-client-id";
		static final String ROLLBAR_CLIENT_SECRET = "rollbar-client-secret";
		static final String ROLLBAR_ENVIRONMENT = "rollbar-environment";
		static final String ENABLE_POSTGRES_DB = "enable-postgres";

		static final String HELP_SHORT = "?";
		static final String HELP_LONG = "help";

	}
}
