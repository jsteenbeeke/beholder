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
package com.jeroensteenbeeke.topiroll.beholder.entities;

import com.jeroensteenbeeke.topiroll.beholder.web.data.visitors.FogOfWarShapeContainsVisitor;

import org.jetbrains.annotations.NotNull;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class FogOfWarShapeVisibility extends FogOfWarVisibility {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "shape")
	private FogOfWarShape shape;

	@NotNull
	public FogOfWarShape getShape() {
		return shape;
	}

	public void setShape(@NotNull FogOfWarShape shape) {
		this.shape = shape;
	}

	@Override
	public boolean containsCoordinate(int x, int y) {
		return getShape().visit(new FogOfWarShapeContainsVisitor(x, y));
	}
}
