/*
 * This file is part of Beholder
 * Copyright (C) 2016 - 2023 Jeroen Steenbeeke
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
import com.jeroensteenbeeke.topiroll.beholder.web.data.shapes.*;

import java.util.List;
import java.util.stream.Collectors;

public class FogOfWarShapeToJSShapeVisitor implements FogOfWarShapeVisitor<JSShape> {
	private static final long serialVersionUID = 4073967800384578318L;
	private final double factor;

	public FogOfWarShapeToJSShapeVisitor(double factor) {
		this.factor = factor;
	}

	@Override
	public JSShape visit(FogOfWarCircle circle) {
		JSCircle circle1 = new JSCircle();
		circle1.setRadius((int) (circle.getRadius()* factor));
		circle1.setX((int) (circle.getOffsetX()* factor));
		circle1.setY((int) (circle.getOffsetY()* factor));
		circle1.setThetaOffset(0.0);
		circle1.setThetaExtent(Math.PI*2);


		return circle1;
	}

	@Override
	public JSShape visit(FogOfWarRect rect) {
		JSRect rect1 = new JSRect();
		rect1.setHeight((int) (rect.getHeight() * factor));
		rect1.setWidth((int) (rect.getWidth() * factor));
		rect1.setX((int) (rect.getOffsetX() * factor));
		rect1.setY((int) (rect.getOffsetY() * factor));

		return rect1;
	}

	@Override
	public JSShape visit(FogOfWarTriangle triangle) {
		List<XY> points = triangle.getOrientation().toPolygon(triangle.getOffsetX(), triangle.getOffsetY(),
				triangle.getHorizontalSide(), triangle.getVerticalSide()).stream().map(p -> p.adjustByFactor(factor))
				.collect(
						Collectors.toList());


		JSPolygon polygon = new JSPolygon();
		polygon.setPoints(points);

		return polygon;
	}
}
