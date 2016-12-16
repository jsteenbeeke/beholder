package com.jeroensteenbeeke.topiroll.beholder.entities;

import java.awt.Graphics2D;
import java.awt.Polygon;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.hyperion.util.ImageUtil;
import com.jeroensteenbeeke.topiroll.beholder.util.JSBuilder;
import com.jeroensteenbeeke.topiroll.beholder.web.resources.AbstractFogOfWarPreviewResource;
import com.jeroensteenbeeke.topiroll.beholder.web.resources.FogOfWarTrianglePreviewResource;

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
	public int getHorizontalSide() {
		return horizontalSide;
	}

	public void setHorizontalSide(@Nonnull int horizontalSide) {
		this.horizontalSide = horizontalSide;
	}

	@Nonnull
	public int getVerticalSide() {
		return verticalSide;
	}

	public void setVerticalSide(@Nonnull int verticalSide) {
		this.verticalSide = verticalSide;
	}

	@Nonnull
	public TriangleOrientation getOrientation() {
		return orientation;
	}

	public void setOrientation(@Nonnull TriangleOrientation orientation) {
		this.orientation = orientation;
	}

	@Override
	public AbstractFogOfWarPreviewResource createThumbnailResource(int size) {
		return new FogOfWarTrianglePreviewResource(ModelMaker.wrap(getMap()),
				this::getHorizontalSide, this::getVerticalSide,
				this::getOffsetX, this::getOffsetY, this::getOrientation) {
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
	public void drawPreviewTo(Graphics2D graphics2D) {
		graphics2D.setColor(FogOfWarShape.TRANSPARENT_BLUE);
		graphics2D.fill(getOrientation().toPolygon(getOffsetX(), getOffsetY(),
				getHorizontalSide(), getVerticalSide()));

	}

	@Override
	public String getDescription() {
		return String.format("%s Triangle (x: %d, y: %d, w: %d, h: %d)",
				getOrientation().getDescription(), getOffsetX(), getOffsetY(),
				getHorizontalSide(), getVerticalSide());
	}

	@Override
	public void renderTo(JSRenderContext context) {
		if (shouldRender(context.getView(), context.isPreviewMode())) {
			final JSBuilder js = context.getJavaScriptBuilder();
			final double multiplier = context.getMultiplier();
			final String contextVariable = context.getContextVariable();

			Polygon poly = getOrientation().toPolygon(getOffsetX(),
					getOffsetY(), getHorizontalSide(), getVerticalSide());

			if (poly.npoints > 0) {
				js.__("%s.moveTo(%d, %d);", contextVariable,
						rel(poly.xpoints[poly.npoints - 1], multiplier),
						rel(poly.ypoints[poly.npoints - 1], multiplier));

				for (int i = 0; i < poly.npoints; i++) {
					int x = rel(poly.xpoints[i], multiplier);
					int y = rel(poly.ypoints[i], multiplier);

					js.__("%s.lineTo(%d,%d);", contextVariable, x, y);
				}
			}

		}
	}
}
