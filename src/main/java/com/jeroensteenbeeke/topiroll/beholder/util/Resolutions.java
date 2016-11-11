package com.jeroensteenbeeke.topiroll.beholder.util;

public enum Resolutions implements Resolution {
	hd1080(1920, 1080), hd720(1280, 720), hd480(852, 480);
	
	private final int width;
	
	private final int height;
	
	
	
	private Resolutions(int width, int height) {
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
