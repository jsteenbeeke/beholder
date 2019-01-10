package com.jeroensteenbeeke.topiroll.beholder.web.data;

public class JSScrollCommand implements JSRenderable {
	private int x;

	private int y;

	public JSScrollCommand() {
	}

	public JSScrollCommand(int x, int y) {
		this.x = x;
		this.y = y;
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

	@Override
	public String getType() {
		return "scroll";
	}
}
