package com.jeroensteenbeeke.topiroll.beholder.entities;

import com.jeroensteenbeeke.topiroll.beholder.BeholderApplication;
import com.jeroensteenbeeke.topiroll.beholder.beans.AmazonData;

public interface AmazonStored {
	String getAmazonKey();

	default String getImageUrl() {
		AmazonData data = BeholderApplication.get().getBean(AmazonData.class);
		final String base = data.getBaseUrl();
		final String prefix = base.endsWith("/") ? base : base.concat("/");

		return prefix.concat(getAmazonKey());
	}
}
