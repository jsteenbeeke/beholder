package com.jeroensteenbeeke.topiroll.beholder.beans.impl;

import com.jeroensteenbeeke.hyperion.rollbar.RollBarReference;
import com.jeroensteenbeeke.topiroll.beholder.beans.RollBarData;
import com.rollbar.notifier.Rollbar;
import com.rollbar.notifier.config.ConfigBuilder;
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
			RollBarReference.instance
					.setRollbar(Rollbar.init(ConfigBuilder.withAccessToken(apiKey).environment(environment).build()));


		} else {
			log.warn("Rollbar not initialized! Errors will not be submitted!");
		}

	}

}
