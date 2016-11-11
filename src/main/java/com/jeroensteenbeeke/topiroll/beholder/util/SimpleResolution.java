package com.jeroensteenbeeke.topiroll.beholder.util;

public class SimpleResolution implements Resolution {
	private final int width;

	private final int height;

	public SimpleResolution(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

}
