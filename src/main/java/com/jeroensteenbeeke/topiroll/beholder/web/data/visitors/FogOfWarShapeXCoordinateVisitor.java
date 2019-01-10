package com.jeroensteenbeeke.topiroll.beholder.web.data.visitors;

import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarCircle;
import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarRect;
import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarTriangle;
import com.jeroensteenbeeke.topiroll.beholder.entities.visitor.FogOfWarShapeVisitor;

public class FogOfWarShapeXCoordinateVisitor implements FogOfWarShapeVisitor<Integer> {
	private static final long serialVersionUID = 3006667075805779680L;

	@Override
	public Integer visit(FogOfWarCircle circle) {
		return circle.getOffsetX() - circle.getRadius();
	}

	@Override
	public Integer visit(FogOfWarRect fogOfWarRect) {
		return fogOfWarRect.getOffsetX();
	}

	@Override
	public Integer visit(FogOfWarTriangle fogOfWarTriangle) {
		return fogOfWarTriangle.getOffsetX();
	}

}
