package com.jeroensteenbeeke.topiroll.beholder.frontend;

import java.util.Optional;

import com.jeroensteenbeeke.hyperion.solitary.InMemory;
import com.jeroensteenbeeke.hyperion.solitary.InMemory.Handler;

public class StartBeholderApplication {
	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.err.println("Usage:");
			System.err.println("\tStartBeholderApplication clientId clientSecret");
			System.exit(-1);
			return;
		}
		
		createApplicationHandler(args).ifPresent(Handler::waitForKeyPress);
	}

	public static Optional<Handler> createApplicationHandler(String[] args) throws Exception {
		return InMemory.run("beholder-web").withContextPath("/beholder/").withProperty("slack.clientid", args[0])
				.withProperty("slack.clientsecret", args[1]).atPort(8081);
	}
}