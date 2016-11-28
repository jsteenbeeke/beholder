package com.jeroensteenbeeke.topiroll.beholder.entities;

import java.awt.Graphics2D;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;

import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.hyperion.util.ImageUtil;
import com.jeroensteenbeeke.topiroll.beholder.util.JSBuilder;
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
	public void renderTo(JSBuilder js, String contextVariable, double multiplier, 
			boolean previewMode) {
		if (shouldRender(previewMode)) {
			js.__("%s.moveTo(%d, %d);", contextVariable, rel(getOffsetX(), multiplier),
					rel(getOffsetY(), multiplier));
			js.__("%s.rect(%d, %d, %d, %d);", contextVariable, rel(getOffsetX(), multiplier),
					rel(getOffsetY(), multiplier), rel(getWidth(), multiplier), rel(getHeight(), multiplier));
		}

	}
}
