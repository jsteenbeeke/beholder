package com.jeroensteenbeeke.topiroll.beholder.beans.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jeroensteenbeeke.topiroll.beholder.beans.RollBarData;

@Component
class RollBarDataImpl implements RollBarData {
	@Value("${rollbar.client.apiKey:}")
	private String clientApiKey;

	@Value("${rollbar.server.apiKey:}")
	private String serverApiKey;

	@Value("${rollbar.environment:}")
	private String environment;

	@Value("${rollbar.localuser}")
	private String localUser;

	@Override
	public String getServerKey() {
		return serverApiKey;
	}

	@Override
	public String getClientKey() {
		return clientApiKey;
	}

	@Override
	public String getEnvironment() {
		return environment;
	}

	@Override
	public String getLocalUsername() {
		return localUser;
	}
}
