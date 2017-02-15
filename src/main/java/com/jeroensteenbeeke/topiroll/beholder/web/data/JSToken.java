package com.jeroensteenbeeke.topiroll.beholder.web.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JSToken  {
	private String src;
	
	private String borderType;
	
	private String borderIntensity;
	
	private String label;
	
	private int x;
	
	private int y;
	
	private int width;
	
	private int height;
	
		
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}



	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	@JsonProperty("border_type")
	public String getBorderType() {
		return borderType;
	}

	public void setBorderType(String borderType) {
		this.borderType = borderType;
	}

	@JsonProperty("border_intensity")
	public String getBorderIntensity() {
		return borderIntensity;
	}

	public void setBorderIntensity(String borderIntensity) {
		this.borderIntensity = borderIntensity;
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
