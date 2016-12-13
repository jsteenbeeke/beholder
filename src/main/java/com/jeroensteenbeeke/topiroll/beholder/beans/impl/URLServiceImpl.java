package com.jeroensteenbeeke.topiroll.beholder.beans.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jeroensteenbeeke.topiroll.beholder.beans.URLService;

@Component
class URLServiceImpl implements URLService {
	@Value("${application.baseurl}")
	private String urlPrefix;

	@Override
	public String contextRelative(String relativePath) {
		String prefix = urlPrefix;

		while (prefix.endsWith("/")) {
			prefix = prefix.substring(0, prefix.length() - 1);
		}

		String path = relativePath;
		while (path.startsWith("/")) {
			path = path.substring(1);
		}

		return String.format("%s/%s", prefix, path);
	}
}
