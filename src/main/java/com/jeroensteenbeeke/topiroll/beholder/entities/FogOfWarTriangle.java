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

import com.jeroensteenbeeke.topiroll.beholder.entities.visitor.FogOfWarShapeVisitor;

import org.jetbrains.annotations.NotNull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

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

	public int getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(int offsetX) {
		this.offsetX = offsetX;
	}

	public int getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(int offsetY) {
		this.offsetY = offsetY;
	}

	public int getHorizontalSide() {
		return horizontalSide;
	}

	public void setHorizontalSide(int horizontalSide) {
		this.horizontalSide = horizontalSide;
	}

	public int getVerticalSide() {
		return verticalSide;
	}

	public void setVerticalSide(int verticalSide) {
		this.verticalSide = verticalSide;
	}

	@NotNull
	public TriangleOrientation getOrientation() {
		return orientation;
	}

	public void setOrientation(@NotNull TriangleOrientation orientation) {
		this.orientation = orientation;
	}


	@Override
	public <T> T visit(@NotNull FogOfWarShapeVisitor<T> visitor) {
		return visitor.visit(this);
	}

	@Override
	public String getDescription() {
		return String.format("%s Triangle (x: %d, y: %d, w: %d, h: %d)",
				getOrientation().getDescription(), getOffsetX(), getOffsetY(),
				getHorizontalSide(), getVerticalSide());
	}

}
