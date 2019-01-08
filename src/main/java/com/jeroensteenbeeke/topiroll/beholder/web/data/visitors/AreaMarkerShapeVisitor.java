package com.jeroensteenbeeke.topiroll.beholder.web.data.visitors;

import com.jeroensteenbeeke.topiroll.beholder.entities.CircleMarker;
import com.jeroensteenbeeke.topiroll.beholder.entities.ConeMarker;
import com.jeroensteenbeeke.topiroll.beholder.entities.CubeMarker;
import com.jeroensteenbeeke.topiroll.beholder.entities.LineMarker;
import com.jeroensteenbeeke.topiroll.beholder.entities.visitor.AreaMarkerVisitor;
import com.jeroensteenbeeke.topiroll.beholder.web.data.shapes.JSCircle;
import com.jeroensteenbeeke.topiroll.beholder.web.data.shapes.JSRect;
import com.jeroensteenbeeke.topiroll.beholder.web.data.shapes.JSShape;

import javax.annotation.Nonnull;

import static com.jeroensteenbeeke.topiroll.beholder.entities.LineMarker.LINE_ANGLE;

public class AreaMarkerShapeVisitor implements AreaMarkerVisitor<JSShape> {
	private static final long serialVersionUID = -6275995536264791711L;

	private final int squareSizeInPixels;

	private final double factor;

	public AreaMarkerShapeVisitor(int squareSizeInPixels, double factor) {
		this.squareSizeInPixels = squareSizeInPixels;
		this.factor = factor;
	}

	@Override
	public JSShape visit(@Nonnull CircleMarker marker) {
		JSCircle circle = new JSCircle();
		int radius = (int) (marker.getExtent() * factor * squareSizeInPixels / 5);

		circle.setRadius(radius);
		// Treat offset as center instead
		circle.setX((int) (marker.getOffsetX() * factor));
		circle.setY((int) (marker.getOffsetY() * factor));
		circle.setThetaOffset(0.0);
		circle.setThetaExtent(Math.PI * 2);

		return circle;
	}

	@Override
	public JSShape visit(@Nonnull ConeMarker marker) {
		JSCircle circle = new JSCircle();
		circle.setRadius((int) (marker.getExtent() * factor * squareSizeInPixels / 5));
		circle.setX((int) (marker.getOffsetX() * factor));
		circle.setY((int) (marker.getOffsetY() * factor));
		circle.setThetaOffset(Math.toRadians((double) marker.getTheta()) - Math.PI / 4);
		circle.setThetaExtent(Math.PI / 2);

		return circle;
	}

	@Override
	public JSShape visit(@Nonnull CubeMarker marker) {
		JSRect rect = new JSRect();
		final int hw = (int) (marker.getExtent() * factor * squareSizeInPixels / 5);
		rect.setHeight(hw);
		rect.setWidth(hw);

		// Note: while this property is called offset, we treat it as the center of the cube
		rect.setX((int) (marker.getOffsetX() * factor));
		rect.setY((int) (marker.getOffsetY() * factor));

		return rect;
	}

	@Override
	public JSShape visit(@Nonnull LineMarker marker) {
		JSCircle circle = new JSCircle();
		circle.setRadius((int) (factor * (marker.getExtent() * squareSizeInPixels / 5)));
		circle.setX((int) (marker.getOffsetX() * factor));
		circle.setY((int) (marker.getOffsetY() * factor));

		double angle = LINE_ANGLE;

		long ext = Math.round(circle.getRadius());

		while (ext > 50) {
			angle = angle * 0.9;
			ext = ext / 2;
		}

		circle.setThetaOffset(Math.toRadians((double) marker.getTheta() - (angle / 2.0)));
		circle.setThetaExtent(Math.toRadians(angle));

		return circle;
	}
}
