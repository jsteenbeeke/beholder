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
package com.jeroensteenbeeke.topiroll.beholder.web.data.visitors;

import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarCircle;
import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarRect;
import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarTriangle;
import com.jeroensteenbeeke.topiroll.beholder.entities.visitor.FogOfWarShapeVisitor;
import com.jeroensteenbeeke.topiroll.beholder.web.data.shapes.XY;

import java.awt.*;
import java.util.List;

public class FogOfWarShapeContainsVisitor implements FogOfWarShapeVisitor<Boolean> {
	private static final long serialVersionUID = 6741879149524777874L;
	private final int x;

	private final int y;

	public FogOfWarShapeContainsVisitor(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public Boolean visit(FogOfWarCircle circle) {
		int cx = circle.getOffsetX() + circle.getRadius();
		int cy = circle.getOffsetY() + circle.getRadius();

		int x_cx = x - cx;
		int y_cy = y - cy;
		int r2 = circle.getRadius() * circle.getRadius();

		return (x_cx * x_cx) + (y_cy * y_cy) < r2;
	}

	@Override
	public Boolean visit(FogOfWarRect rect) {
		int x2 = rect.getOffsetX() + rect.getWidth();
		int y2 = rect.getOffsetY() + rect.getHeight();

		return x >= rect.getOffsetX() && x <= x2 && y >= rect.getOffsetY() && y <= y2;
	}

	@Override
	public Boolean visit(FogOfWarTriangle triangle) {
		List<XY> xyList = triangle.getOrientation().toPolygon(triangle.getOffsetX(), triangle.getOffsetY(),
				triangle.getHorizontalSide(), triangle.getVerticalSide());

		Polygon poly = new Polygon();
		xyList.forEach(xy -> poly.addPoint(xy.getX(), xy.getY()));

		return poly.contains(x, y);
	}
}
