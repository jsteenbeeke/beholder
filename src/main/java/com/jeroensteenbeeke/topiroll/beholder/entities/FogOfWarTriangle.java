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
import com.jeroensteenbeeke.topiroll.beholder.web.data.shapes.JSPolygon;
import com.jeroensteenbeeke.topiroll.beholder.web.data.shapes.JSShape;
import com.jeroensteenbeeke.topiroll.beholder.web.data.shapes.XY;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

@Entity
public class FogOfWarTriangle extends FogOfWarShape {

	private static final long serialVersionUID = 1L;

	@Column(nullable = false)
	private int offsetY;

	@Column(nullable = false)
	private int horizontalSide;

	@Column(nullable = false)
	private int verticalSide;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private TriangleOrientation orientation;

	@Column(nullable = false)
	private int offsetX;

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

	@Nonnull
	public int getHorizontalSide() {
		return horizontalSide;
	}

	public void setHorizontalSide(@Nonnull int horizontalSide) {
		this.horizontalSide = horizontalSide;
	}

	@Nonnull
	public int getVerticalSide() {
		return verticalSide;
	}

	public void setVerticalSide(@Nonnull int verticalSide) {
		this.verticalSide = verticalSide;
	}

	@Nonnull
	public TriangleOrientation getOrientation() {
		return orientation;
	}

	public void setOrientation(@Nonnull TriangleOrientation orientation) {
		this.orientation = orientation;
	}


	@Override
	public <T> T visit(@Nonnull FogOfWarShapeVisitor<T> visitor) {
		return visitor.visit(this);
	}

	@Override
	public String getDescription() {
		return String.format("%s Triangle (x: %d, y: %d, w: %d, h: %d)",
				getOrientation().getDescription(), getOffsetX(), getOffsetY(),
				getHorizontalSide(), getVerticalSide());
	}

	@Override
	public boolean containsCoordinate(int x, int y) {
		List<XY> xyList = getOrientation().toPolygon(getOffsetX(), getOffsetY(),
				getHorizontalSide(), getVerticalSide());

		Polygon poly = new Polygon();
		xyList.forEach(xy -> poly.addPoint(xy.getX(), xy.getY()));

		return poly.contains(x, y);
	}

	@Override
	public JSShape toJS(double factor) {
		List<XY> points = getOrientation().toPolygon(getOffsetX(), getOffsetY(),
				getHorizontalSide(), getVerticalSide());

		JSPolygon polygon = new JSPolygon();
		polygon.setPoints(points);

		return polygon;
	}
}
