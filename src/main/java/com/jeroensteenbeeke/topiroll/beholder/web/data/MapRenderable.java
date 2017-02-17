/**
 * This file is part of Beholder
 * (C) 2016 Jeroen Steenbeeke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jeroensteenbeeke.topiroll.beholder.web.data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jeroensteenbeeke.topiroll.beholder.web.data.shapes.JSShape;

public class MapRenderable implements JSRenderable {
	private String src;

	private int width;

	private int height;

	private List<JSShape> revealed;

	private List<JSToken> tokens;

	@JsonProperty("area_markers")
	private List<JSAreaMarker> areaMarkers;

	@Override
	public String getType() {
		return "map";
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
