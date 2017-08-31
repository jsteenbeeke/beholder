package com.jeroensteenbeeke.topiroll.beholder.web.data;

import com.jeroensteenbeeke.topiroll.beholder.BeholderApplication;
import com.jeroensteenbeeke.topiroll.beholder.beans.URLService;
import com.jeroensteenbeeke.topiroll.beholder.entities.PortraitVisibility;

public class JSPortrait {
	private String location;

	private String url;

	public JSPortrait() {
	}

	public JSPortrait(PortraitVisibility portraitVisibility) {
		this.url = BeholderApplication.get().getBean(URLService.class).contextRelative(String.format("portraits/%d", portraitVisibility.getPortrait().getId()));
		this.location = portraitVisibility.getLocation().name().toLowerCase();

	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
