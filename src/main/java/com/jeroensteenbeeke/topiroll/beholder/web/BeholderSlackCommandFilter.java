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
