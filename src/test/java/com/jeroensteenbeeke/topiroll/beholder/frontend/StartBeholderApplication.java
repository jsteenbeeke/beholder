/**
 * This file is part of Beholder
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
				.withProperty("slack.clientsecret", args[1]).withProperty("url.prefix", "http://localhost:8081/beholder/").atPort(8081);
	}
}