package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster;

import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarCircle;
import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarRect;
import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarTriangle;
import com.jeroensteenbeeke.topiroll.beholder.entities.visitors.FogOfWarShapeVisitor;
import com.jeroensteenbeeke.topiroll.beholder.web.data.shapes.XY;

import java.util.List;
import java.util.stream.Collectors;

public class FogOfWarPreviewRenderer implements FogOfWarShapeVisitor<String> {
	private final String canvasId;

	private final double factor;

	public FogOfWarPreviewRenderer(String canvasId, double factor) {
		this.canvasId = canvasId;
		this.factor = factor;
	}

	@Override
	public String visit(FogOfWarCircle circle) {
		return String.format("previewCircle('%s', 'rgba(0,0,255,0.5)', {'x': %d, 'y': %d, 'radius': %d, 'theta_offset': 0, 'theta_extent': (Math.PI*2)});\n",
				canvasId, scaled(circle.getOffsetX()), scaled(circle.getOffsetY()), scaled(circle.getRadius())
		);
	}

	@Override
	public String visit(FogOfWarRect rect) {
		return String.format("previewRectangle('%s', 'rgba(0,0,255,0.5)', { 'x': %d, 'y': %d, 'width': %d, 'height': %d });\n",
				canvasId,
				scaled(rect.getOffsetX()), scaled(rect.getOffsetY()), scaled(rect.getWidth()), scaled(rect.getHeight())
		);

	}

	private int scaled(int unit) {
		return (int) ((double) unit * factor);
	}

	@Override
	public String visit(FogOfWarTriangle triangle) {

		List<XY> poly = triangle.getOrientation()
				.toPolygon(scaled(triangle.getOffsetX()), scaled(triangle.getOffsetY()),
						scaled(triangle.getHorizontalSide()),
						scaled(triangle.getVerticalSide()));

		return String.format("previewPolygon('%s', 'rgba(0,0,255,0.5)', { points: [%s] });\n", canvasId,
				poly.stream().map(xy -> String.format("{'x': %d, 'y': %d}", xy.getX(), xy.getY())).collect(
						Collectors.joining(", ")));
	}
}
