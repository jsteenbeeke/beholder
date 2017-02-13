package com.jeroensteenbeeke.topiroll.beholder.web.data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jeroensteenbeeke.topiroll.beholder.web.data.shapes.JSShape;

public class MapRenderable implements JSRenderable {
	private String src;

	private List<JSShape> revealed;
	
	private List<JSToken> tokens;
	
	@JsonProperty("area_markers")
	private List<JSAreaMarker> areaMarkers;
	
	@Override
	public String getType() {
		return "map";
	}

	public String getSrc() {
		return src;
	}
	
	public void setSrc(String src) {
		this.src = src;
	}
	
	public List<JSShape> getRevealed() {
		return revealed;
	}
	
	public void setRevealed(List<JSShape> revealed) {
		this.revealed = revealed;
	}

	public List<JSToken> getTokens() {
		return tokens;
	}

	public void setTokens(List<JSToken> tokens) {
		this.tokens = tokens;
	}

	public List<JSAreaMarker> getAreaMarkers() {
		return areaMarkers;
	}

	public void setAreaMarkers(List<JSAreaMarker> areaMarkers) {
		this.areaMarkers = areaMarkers;
	}
	
	
}
