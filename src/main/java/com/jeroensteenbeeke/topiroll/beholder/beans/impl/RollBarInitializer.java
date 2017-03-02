package com.jeroensteenbeeke.topiroll.beholder.beans.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jeroensteenbeeke.hyperion.rollbar.RollBarReference;
import com.jeroensteenbeeke.topiroll.beholder.beans.RollBarData;
import com.rollbar.Rollbar;

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
			RollBarReference.instance
					.setRollbar(new Rollbar(apiKey, environment));
		} else {
			log.warn("Rollbar not initialized! Errors will not be submitted!");
		}

	}

}