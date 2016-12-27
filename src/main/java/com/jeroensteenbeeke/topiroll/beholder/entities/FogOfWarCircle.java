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

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;

import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.hyperion.util.ImageUtil;
import com.jeroensteenbeeke.topiroll.beholder.util.JSBuilder;
import com.jeroensteenbeeke.topiroll.beholder.web.resources.AbstractFogOfWarPreviewResource;
import com.jeroensteenbeeke.topiroll.beholder.web.resources.FogOfWarCirclePreviewResource;

@Entity
public class FogOfWarCircle extends FogOfWarShape {

	private static final long serialVersionUID = 1L;

	@Column(nullable = false)
	private int radius;

	@Column(nullable = false)
	private int offsetY;

	@Column(nullable = false)
	private int offsetX;

	@Nonnull
	public int getRadius() {
		return radius;
	}

	public void setRadius(@Nonnull int radius) {
		this.radius = radius;
	}

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

	@Override
	public String getDescription() {
		return String.format("Circle (x: %d, y: %d, r: %d)", getOffsetX(),
				getOffsetY(), getRadius());
	}

	@Override
	public void drawPreviewTo(Graphics2D graphics2d) {
		graphics2d.setColor(TRANSPARENT_BLUE);
		Shape circle = new Ellipse2D.Double(getOffsetX(), getOffsetY(),
				2.0 * getRadius(), 2.0 * getRadius());
		graphics2d.fill(circle);

	}
	
	@Override
	public boolean containsCoordinate(int x, int y) {
		int cx = getOffsetX()+getRadius();
		int cy = getOffsetY()+getRadius();
		
		int x_cx = x - cx;
		int y_cy = y - cy;
		int r2 = getRadius() * getRadius();
		
		return (x_cx * x_cx) + (y_cy * y_cy) < r2;
	}

	@Override
	public AbstractFogOfWarPreviewResource createThumbnailResource(int size) {

		return new FogOfWarCirclePreviewResource(ModelMaker.wrap(getMap()),
				this::getRadius, this::getOffsetX, this::getOffsetY) {
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean shouldDrawExistingShapes() {
				return false;
			}

			@Override
			protected byte[] postProcess(byte[] image) {

				return ImageUtil.resize(image, size, size);
			}

		};
	}

	@Override
	public void renderTo(@Nonnull JSRenderContext context) {

		if (shouldRender(context.getView(), context.isPreviewMode())) {
			final JSBuilder js = context.getJavaScriptBuilder();
			final double multiplier = context.getMultiplier();
			final String contextVariable = context.getContextVariable();

			js.__("%s.moveTo(%d, %d);", contextVariable,
					rel(getOffsetX(), multiplier),
					rel(getOffsetY(), multiplier));
			js.__("%s.arc(%d, %d, %d, 0, 2 * Math.PI);", contextVariable,
					rel(getOffsetX() + getRadius(), multiplier),
					rel(getOffsetY() + getRadius(), multiplier),
					rel(getRadius(), multiplier));
		}

	}

}
