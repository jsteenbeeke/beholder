package com.jeroensteenbeeke.topiroll.beholder.entities;

import com.jeroensteenbeeke.topiroll.beholder.BeholderApplication;
import com.jeroensteenbeeke.topiroll.beholder.beans.RemoteImageData;

public interface AmazonStored {
	String getAmazonKey();

	default String getImageUrl() {
		RemoteImageData data = BeholderApplication.get().getBean(RemoteImageData.class);
		final String base = data.getBaseUrl();
		final String prefix = base.endsWith("/") ? base : base.concat("/");

		return prefix.concat(getAmazonKey());
	}
}
