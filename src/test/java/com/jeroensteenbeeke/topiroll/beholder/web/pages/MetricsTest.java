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
package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import com.jeroensteenbeeke.hyperion.solitary.InMemory;
import com.jeroensteenbeeke.hyperion.wicket.rest.test.AbstractFrontendTest;
import com.jeroensteenbeeke.topiroll.beholder.frontend.StartBeholderApplication;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

public class MetricsTest extends AbstractFrontendTest {
	@Test
	public void testMetricsEndpoint() throws IOException {

		URLConnection conn = new URL("http://localhost:8081/beholder/metrics").openConnection();

		try (InputStream response = conn.getInputStream(); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			IOUtils.copy(response, bos);
			bos.flush();

			String metrics = bos.toString();

			assertThat(metrics, startsWith("# HELP"));
		}
	}

	@Override
	protected InMemory.Handler createApplicationHandler() throws Exception {
		return StartBeholderApplication.createApplicationHandler(new String[0])
									   .orElseThrow(IllegalStateException::new);
	}

	@Override
	protected String getApplicationKey() {
		return "wicket.BeholderApplication";
	}
}
