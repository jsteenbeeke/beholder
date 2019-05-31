package com.jeroensteenbeeke.topiroll.beholder.web.data;

public class RingDoorbell implements JSRenderable {
	private final String username;

	public RingDoorbell(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	@Override
	public String getType() {
		return "doorbell";
	}
}
