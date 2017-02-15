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

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;

import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.hyperion.util.ImageUtil;
import com.jeroensteenbeeke.topiroll.beholder.util.JSBuilder;
import com.jeroensteenbeeke.topiroll.beholder.web.data.shapes.JSRect;
import com.jeroensteenbeeke.topiroll.beholder.web.data.shapes.JSShape;
import com.jeroensteenbeeke.topiroll.beholder.web.resources.AbstractFogOfWarPreviewResource;
import com.jeroensteenbeeke.topiroll.beholder.web.resources.FogOfWarRectPreviewResource;

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
	public void drawPreviewTo(Graphics2D graphics2d) {
		graphics2d.setColor(FogOfWarShape.TRANSPARENT_BLUE);
		graphics2d.fillRect(getOffsetX(), getOffsetY(), getWidth(),
				getHeight());
	}

	@Override
	public AbstractFogOfWarPreviewResource createThumbnailResource(int size) {

		return new FogOfWarRectPreviewResource(ModelMaker.wrap(getMap()),
				this::getWidth, this::getHeight, this::getOffsetX,
				this::getOffsetY) {
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
			js.__("%s.rect(%d, %d, %d, %d);", contextVariable,
					rel(getOffsetX(), multiplier),
					rel(getOffsetY(), multiplier), rel(getWidth(), multiplier),
					rel(getHeight(), multiplier));
		}
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
