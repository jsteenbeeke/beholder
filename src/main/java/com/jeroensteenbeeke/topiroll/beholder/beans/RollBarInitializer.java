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
package com.jeroensteenbeeke.topiroll.beholder.beans;

import com.jeroensteenbeeke.hyperion.rollbar.RollBarReference;
import com.jeroensteenbeeke.topiroll.beholder.beans.RollBarData;
import com.rollbar.notifier.Rollbar;
import com.rollbar.notifier.config.ConfigBuilder;
import org.apache.wicket.protocol.http.PageExpiredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RollBarInitializer implements InitializingBean {
	private static final Logger log = LoggerFactory
			.getLogger(RollBarInitializer.class);

	@Autowired
	private RollBarData data;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		String apiKey = data.getServerKey();
		String environment = data.getEnvironment();

		log.info("Rollbar API key: {}", apiKey != null && !apiKey.isEmpty());
		log.info("Rollbar Environment: {}", environment);

		if (apiKey != null && !apiKey.isEmpty() && environment != null
				&& !environment.isEmpty()) {
			RollBarReference.instance.excludeException("org.eclipse.jetty.io.EofException");
			RollBarReference.instance.excludeException("org.apache.wicket.IWicketInternalException");
			RollBarReference.instance.excludeException(PageExpiredException.class);
			RollBarReference.instance
					.setRollbar(Rollbar.init(ConfigBuilder.withAccessToken(apiKey).environment(environment).build()));


		} else {
			log.warn("Rollbar not initialized! Errors will not be submitted!");
		}

	}

}
