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
		Shape circle = new Ellipse2D.Double(getOffsetX(),
				getOffsetY(), 2.0 * getRadius(),
				2.0 * getRadius());
		graphics2d.fill(circle);

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
	public void renderTo(JSBuilder js, String contextVariable, double multiplier,
			boolean previewMode) {
		if (shouldRender(previewMode)) {
			js.__("%s.moveTo(%d, %d);", contextVariable, rel(getOffsetX(), multiplier),
					rel(getOffsetY(), multiplier));
			js.__("%s.arc(%d, %d, %d, 0, 2 * Math.PI);", contextVariable,
					rel(getOffsetX() + getRadius(), multiplier), rel(getOffsetY() + getRadius(), multiplier), rel(getRadius(), multiplier));
		}

	}

}
