package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import com.jeroensteenbeeke.hyperion.solitary.InMemory;
import com.jeroensteenbeeke.hyperion.wicket.rest.test.AbstractFrontendTest;
import com.jeroensteenbeeke.topiroll.beholder.frontend.StartBeholderApplication;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

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
