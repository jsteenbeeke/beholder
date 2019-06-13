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
