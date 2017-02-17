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
