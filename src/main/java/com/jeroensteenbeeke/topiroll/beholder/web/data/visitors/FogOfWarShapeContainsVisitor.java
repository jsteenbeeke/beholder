package com.jeroensteenbeeke.topiroll.beholder.web.data.visitors;

import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarCircle;
import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarRect;
import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarTriangle;
import com.jeroensteenbeeke.topiroll.beholder.entities.visitor.FogOfWarShapeVisitor;
import com.jeroensteenbeeke.topiroll.beholder.web.data.shapes.XY;

import java.awt.*;
import java.util.List;

public class FogOfWarShapeContainsVisitor implements FogOfWarShapeVisitor<Boolean> {
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
