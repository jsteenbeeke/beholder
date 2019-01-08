package com.jeroensteenbeeke.topiroll.beholder.entities.visitor;

import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarCircle;
import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarRect;
import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarTriangle;

import java.io.Serializable;

public interface FogOfWarShapeVisitor<T> extends Serializable {

	T visit(FogOfWarCircle fogOfWarCircle);

	T visit(FogOfWarRect fogOfWarRect);

	T visit(FogOfWarTriangle fogOfWarTriangle);
}
