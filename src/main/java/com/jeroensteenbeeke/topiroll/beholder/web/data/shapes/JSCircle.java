package com.jeroensteenbeeke.topiroll.beholder.web.data.shapes;

public class JSCircle implements JSShape {
	private int x;
	
	private int y;
	
	private int radius;
	
	@Override
	public String getType() {
		return "circle";
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}
	
	

}
