package com.jeroensteenbeeke.topiroll.beholder.web.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Payload {
	private String canvasId;
	
	private JSRenderable data;

	@JsonProperty("canvas_id")
	public String getCanvasId() {
		return canvasId;
	}

	public void setCanvasId(String canvasId) {
		this.canvasId = canvasId;
	}

	public JSRenderable getData() {
		return data;
	}

	public void setData(JSRenderable data) {
		this.data = data;
	}
	
	
}
