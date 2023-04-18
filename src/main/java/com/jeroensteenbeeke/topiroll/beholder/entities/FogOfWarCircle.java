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
import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class FogOfWarCircle extends FogOfWarShape {

	private static final long serialVersionUID = 1L;

	@Column(nullable = false)
	private int radius;

	@Column(nullable = false)
	private int offsetY;

	@Column(nullable = false)
	private int offsetX;


	public int getRadius() {
		return radius;
	}

	public void setRadius( int radius) {
		this.radius = radius;
	}


	public int getOffsetX() {
		return offsetX;
	}

	public void setOffsetX( int offsetX) {
		this.offsetX = offsetX;
	}


	public int getOffsetY() {
		return offsetY;
	}

	public void setOffsetY( int offsetY) {
		this.offsetY = offsetY;
	}

	@Override
	public String getDescription() {
		return String.format("Circle (x: %d, y: %d, r: %d)", getOffsetX(),
				getOffsetY(), getRadius());
	}


	@Override
	public <T> T visit(@NotNull FogOfWarShapeVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
