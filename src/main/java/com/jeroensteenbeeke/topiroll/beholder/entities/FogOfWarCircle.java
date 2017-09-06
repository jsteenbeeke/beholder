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
package com.jeroensteenbeeke.topiroll.beholder.entities;

import com.jeroensteenbeeke.topiroll.beholder.entities.visitors.FogOfWarShapeVisitor;
import com.jeroensteenbeeke.topiroll.beholder.web.data.shapes.JSCircle;
import com.jeroensteenbeeke.topiroll.beholder.web.data.shapes.JSShape;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class FogOfWarCircle extends FogOfWarShape {

	private static final long serialVersionUID = 1L;

	@Column(nullable = false)
	private int radius;

	@Column(nullable = false)
	private int offsetY;

	@Column(nullable = false)
	private int offsetX;

	@Nonnull
	public int getRadius() {
		return radius;
	}

	public void setRadius(@Nonnull int radius) {
		this.radius = radius;
	}

	@Nonnull
	public int getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(@Nonnull int offsetX) {
		this.offsetX = offsetX;
	}

	@Nonnull
	public int getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(@Nonnull int offsetY) {
		this.offsetY = offsetY;
	}

	@Override
	public String getDescription() {
		return String.format("Circle (x: %d, y: %d, r: %d)", getOffsetX(),
				getOffsetY(), getRadius());
	}

	@Override
	public boolean containsCoordinate(int x, int y) {
		int cx = getOffsetX()+getRadius();
		int cy = getOffsetY()+getRadius();
		
		int x_cx = x - cx;
		int y_cy = y - cy;
		int r2 = getRadius() * getRadius();
		
		return (x_cx * x_cx) + (y_cy * y_cy) < r2;
	}


	@Override
	public <T> T visit(@Nonnull FogOfWarShapeVisitor<T> visitor) {
		return visitor.visit(this);
	}

	@Override
	public JSShape toJS(double factor) {
		JSCircle circle = new JSCircle();
		circle.setRadius((int) (getRadius()*factor));
		circle.setX((int) (getOffsetX()*factor));
		circle.setY((int) (getOffsetY()*factor));
		circle.setThetaOffset(0.0);
		circle.setThetaExtent(Math.PI*2);


		return circle;
	}
}
