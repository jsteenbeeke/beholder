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
package com.jeroensteenbeeke.topiroll.beholder.web;

import com.jeroensteenbeeke.hyperion.social.web.filter.SlackCommandFilter;
import com.jeroensteenbeeke.lux.TypedResult;
import com.jeroensteenbeeke.topiroll.beholder.BeholderApplication;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.IOException;
import java.io.PrintWriter;

public class BeholderSlackCommandFilter extends SlackCommandFilter {
	private static final Logger log = LoggerFactory.getLogger(BeholderSlackCommandFilter.class);

	@Override
	public void onSlackCommand(SlackCommandContext context) {
		if ("/doorbell".equals(context.getCommand())) {
			BeholderApplication
				.get()
				.getBean(MapService.class)
				.doorbell(context.getParameter("user_name").getOrElse("??unknown??"))
				.peek(instances -> {
					if (instances == 0) {
						context
							.postResponse("You rang the doorbell, but nobody is listening")
							.ofType(SlackResponseType.Ephemeral)
							.ifNotOk(log::error);
					} else {
						context
							.postResponse("Doorbell has been rung")
							.ofType(SlackResponseType.Ephemeral)
							.ifNotOk(log::error);
					}
				})
				.ifNotOk(msg -> context
					.postResponse("Could not ring doorbell: " + msg)
					.ofType(SlackResponseType.Ephemeral).ifNotOk(log::error)
				);
			;

		}
	}
}
