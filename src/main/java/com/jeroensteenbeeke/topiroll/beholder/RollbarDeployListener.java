package com.jeroensteenbeeke.topiroll.beholder;

import com.jeroensteenbeeke.topiroll.beholder.beans.RollBarData;
import okhttp3.*;
import org.apache.wicket.Application;
import org.apache.wicket.IApplicationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class RollbarDeployListener implements IApplicationListener {
	private static final Logger log = LoggerFactory.getLogger(RollbarDeployListener.class);

	private final RollBarData data;

	RollbarDeployListener(RollBarData data) {
		this.data = data;
	}

	@Override
	public void onAfterInitialized(Application application) {
		if (data.getEnvironment() != null) {
			OkHttpClient client = new OkHttpClient();
			Request request = new Request.Builder().url("https://api.rollbar.com/api/1/deploy/")
												   .post(new MultipartBody.Builder()
												   .addFormDataPart("access_token", data.getServerKey())
												   .addFormDataPart("environment", data.getEnvironment())
												   .addFormDataPart("revision", getRevision())
												   .addFormDataPart("local_username", data.getLocalUsername())
												   .setType(MultipartBody.FORM)
														   	   .build()

												   ).build();
			try {
				Response response = client.newCall(request).execute();

				if (response.isSuccessful()) {
					log.info("Rollbar deploy notification done");
				} else {
					log.info("Rollbar deploy notification failed: {} {}", response.code(), response.body().string());
				}
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
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

			return new String(bos.toByteArray(), "UTF-8");
		} catch (IOException ioe) {
			return "Unknown";
		}
	}

	@Override
	public void onBeforeDestroyed(Application application) {

	}
}
