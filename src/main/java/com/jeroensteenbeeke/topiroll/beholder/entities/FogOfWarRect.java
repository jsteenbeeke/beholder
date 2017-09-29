/**
 * This file is part of Beholder
 * (C) 2016 Jeroen Steenbeeke
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

import com.jeroensteenbeeke.topiroll.beholder.entities.visitors.FogOfWarShapeVisitor;
import com.jeroensteenbeeke.topiroll.beholder.web.data.shapes.JSRect;
import com.jeroensteenbeeke.topiroll.beholder.web.data.shapes.JSShape;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class FogOfWarRect extends FogOfWarShape {

	private static final long serialVersionUID = 1L;

	@Column(nullable = false)
	private int offsetY;

	@Column(nullable = false)
	private int width;

	@Column(nullable = false)
	private int height;

	@Column(nullable = false)
	private int offsetX;

	@Nonnull
	public int getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(@Nonnull int offsetX) {
		this.offsetX = offsetX;
	}

	@Nonnull
	public int getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(@Nonnull int offsetY) {
		this.offsetY = offsetY;
	}

	@Nonnull
	public int getWidth() {
		return width;
	}

	public void setWidth(@Nonnull int width) {
		this.width = width;
	}

	@Nonnull
	public int getHeight() {
		return height;
	}

	public void setHeight(@Nonnull int height) {
		this.height = height;
	}

	@Override
	public String getDescription() {
		return String.format("Rectangle (x: %d, y: %d, w: %d, h: %d)",
				getOffsetX(), getOffsetY(), getWidth(), getHeight());
	}


	@Override
	public <T> T visit(@Nonnull FogOfWarShapeVisitor<T> visitor) {
		return visitor.visit(this);
	}

	@Override
	public boolean containsCoordinate(int x, int y) {
		int x2 = getOffsetX() + getWidth();
		int y2 = getOffsetY() + getHeight();
		
		return x >= getOffsetX() && x <= x2 && y >= getOffsetY() && y <= y2;
	}
	
	@Override
	public JSShape toJS(double factor) {
		JSRect rect = new JSRect();
		rect.setHeight((int) (getHeight() * factor));
		rect.setWidth((int) (getWidth() * factor));
		rect.setX((int) (getOffsetX() * factor));
		rect.setY((int) (getOffsetY() * factor));

		return rect;
	}
}
