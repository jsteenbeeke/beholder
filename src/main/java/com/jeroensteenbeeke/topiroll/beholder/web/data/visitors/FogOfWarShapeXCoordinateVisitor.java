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
