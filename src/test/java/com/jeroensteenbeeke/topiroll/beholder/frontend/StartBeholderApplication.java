/**
 * This file is part of Beholder
 * (C) 2016 Jeroen Steenbeeke
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

import java.util.Optional;
import java.util.function.Consumer;

import javax.servlet.ServletException;
import javax.websocket.DeploymentException;

import org.apache.wicket.protocol.ws.javax.WicketServerEndpointConfig;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.websocket.jsr356.server.ServerContainer;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

import com.jeroensteenbeeke.hyperion.solitary.InMemory;
import com.jeroensteenbeeke.hyperion.solitary.InMemory.Handler;

public class StartBeholderApplication {
	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.err.println("Usage:");
			System.err.println(
					"\tStartBeholderApplication clientId clientSecret");
			System.exit(-1);
			return;
		}

		createApplicationHandler(args).ifPresent(Handler::waitForKeyPress);
	}

	public static Optional<Handler> createApplicationHandler(String[] args)
			throws Exception {
		Consumer<WebAppContext> initWebsockets = context -> {
			try {
				ServerContainer wscontainer = WebSocketServerContainerInitializer
						.configureContext(context);

				wscontainer.addEndpoint(new WicketServerEndpointConfig());
			} catch (DeploymentException | ServletException e) {
				e.printStackTrace();
			}
		};

		return InMemory.run("beholder-web").withContextPath("/beholder/")
				.withContextConsumer(initWebsockets)
				.withProperty("slack.clientid", args[0])
				.withProperty("slack.clientsecret", args[1])
				.withProperty("application.baseurl",
						"http://localhost:8081/beholder/")
				.withProperty("application.sourceurl",
						"file://" + System.getProperty("user.dir"))
				.atPort(8081);
	}
}