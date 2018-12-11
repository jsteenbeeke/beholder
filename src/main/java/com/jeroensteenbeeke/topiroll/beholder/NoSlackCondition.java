package com.jeroensteenbeeke.topiroll.beholder;

import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class NoSlackCondition implements ConfigurationCondition {
	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		String property = context.getEnvironment().getProperty("slack.login.disabled");
		return property != null && property.equals(Boolean.TRUE.toString());

	}

	@Override
	public ConfigurationPhase getConfigurationPhase() {
		return ConfigurationPhase.REGISTER_BEAN;
	}
}
