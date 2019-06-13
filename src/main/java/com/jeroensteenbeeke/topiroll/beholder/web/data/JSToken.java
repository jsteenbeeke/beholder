/**
 * This file is part of Beholder
 * (C) 2016-2019 Jeroen Steenbeeke
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

	private int diameterInSquares;
	
		
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

	@JsonProperty("diameter_in_squares")
	public int getDiameterInSquares() {
		return diameterInSquares;
	}

	public void setDiameterInSquares(int diameterInSquares) {
		this.diameterInSquares = diameterInSquares;
	}
}
