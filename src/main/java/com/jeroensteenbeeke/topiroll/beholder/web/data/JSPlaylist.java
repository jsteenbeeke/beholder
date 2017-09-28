package com.jeroensteenbeeke.topiroll.beholder.web.data;

public class JSPlaylist implements JSRenderable {
	private String url;

	public JSPlaylist() {
	}

	public JSPlaylist(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String getType() {
		return "youtube";
	}
}
