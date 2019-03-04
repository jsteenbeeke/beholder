package com.jeroensteenbeeke.topiroll.beholder;

import com.jeroensteenbeeke.hyperion.Hyperion;
import com.jeroensteenbeeke.hyperion.rollbar.RollBarDeployNotifier;
import com.jeroensteenbeeke.topiroll.beholder.beans.RollBarData;
import org.apache.wicket.Application;
import org.apache.wicket.IApplicationListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class RollbarDeployListener implements IApplicationListener {
	private final RollBarData data;

	RollbarDeployListener(RollBarData data) {
		this.data = data;
	}

	@Override
	public void onAfterInitialized(Application application) {
		if (data.getEnvironment() != null) {
			RollBarDeployNotifier.createNotifier().withApiKey(data.getServerKey())
								 .withEnvironment(data.getEnvironment())
								 .andDeployingUser(data.getLocalUsername())
								 .notifyDeploy(String.format("beholder-%s-hyperion-%s", getRevision(), Hyperion.getRevision().getOrElse("unknown")));
		}
	}

	private String getRevision() {
		try (InputStream stream = RollbarDeployListener.class.getResourceAsStream("revision.txt"); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			int i;

			if (stream == null) {
				return "Unknown";
			}

			while ((i = stream.read()) != -1) {
				bos.write(i);
			}

			return new String(bos.toByteArray(), StandardCharsets.UTF_8);
		} catch (IOException ioe) {
			return "Unknown";
		}
	}

	@Override
	public void onBeforeDestroyed(Application application) {

	}
}
