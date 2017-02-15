package com.jeroensteenbeeke.topiroll.beholder.web.data.shapes;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JSCircle implements JSShape {
	private int x;
	
	private int y;
	
	private int radius;
	
	private double thetaOffset;
	
	private double thetaExtent;
	
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

	@JsonProperty("theta_offset")
	public double getThetaOffset() {
		return thetaOffset;
	}

	public void setThetaOffset(double thetaOffset) {
		this.thetaOffset = thetaOffset;
	}

	@JsonProperty("theta_extent")
	public double getThetaExtent() {
		return thetaExtent;
	}

	public void setThetaExtent(double thetaExtent) {
		this.thetaExtent = thetaExtent;
	}
	
	

}
