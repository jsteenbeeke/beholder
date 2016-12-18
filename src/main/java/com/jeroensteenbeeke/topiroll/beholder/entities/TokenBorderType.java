package com.jeroensteenbeeke.topiroll.beholder.entities;

public enum TokenBorderType {
	Neutral(255, 255, 0), Ally(0, 0, 255), Enemy(255, 0, 0);
	
	private final int red;
	
	private final int green;
	
	private final int blue;

	private TokenBorderType(int red, int green, int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public int getRed() {
		return red;
	}
	
	public int getGreen() {
		return green;
	}
	
	public int getBlue() {
		return blue;
	}
	
	
}
