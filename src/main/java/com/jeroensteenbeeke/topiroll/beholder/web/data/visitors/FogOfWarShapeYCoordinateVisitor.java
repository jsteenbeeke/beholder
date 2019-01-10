package com.jeroensteenbeeke.topiroll.beholder.web.data.visitors;

import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarCircle;
import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarRect;
import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarTriangle;
import com.jeroensteenbeeke.topiroll.beholder.entities.visitor.FogOfWarShapeVisitor;

public class FogOfWarShapeYCoordinateVisitor implements FogOfWarShapeVisitor<Integer> {

	private static final long serialVersionUID = -1015157163565217084L;

	@Override
	public Integer visit(FogOfWarCircle circle) {
		return circle.getOffsetY() - circle.getRadius();
	}

	@Override
	public Integer visit(FogOfWarRect fogOfWarRect) {
		return fogOfWarRect.getOffsetY();
	}

	@Override
	public Integer visit(FogOfWarTriangle fogOfWarTriangle) {
		return fogOfWarTriangle.getOffsetY();
	}

}
