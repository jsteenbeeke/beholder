package com.jeroensteenbeeke.topiroll.beholder.web.data.shapes;

public class JSRect implements JSShape {
	private int x;
	
	private int y;
	
	private int width;
	
	private int height;
	
	@Override
	public String getType() {
		return "rect";
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

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	

}
