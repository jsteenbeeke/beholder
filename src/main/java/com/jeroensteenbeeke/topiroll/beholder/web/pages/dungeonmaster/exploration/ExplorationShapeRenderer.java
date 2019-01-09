package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.exploration;

import com.jeroensteenbeeke.topiroll.beholder.entities.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.visitor.FogOfWarShapeVisitor;
import com.jeroensteenbeeke.topiroll.beholder.web.data.shapes.XY;

import java.util.List;
import java.util.stream.Collectors;

public class ExplorationShapeRenderer implements FogOfWarShapeVisitor<String> {
	private static final String COLOR_INVISIBLE = "#ff0000";

	private static final String COLOR_DM_ONLY = "#0000ff";

	private static final String COLOR_VISIBLE = "#00ff00";

	private final String canvasId;

	private final double factor;

	private final MapView view;

	public ExplorationShapeRenderer(String canvasId, double factor, MapView view) {
		this.canvasId = canvasId;
		this.factor = factor;
		this.view = view;
	}

	@Override
	public String visit(FogOfWarCircle circle) {
		return String.format("previewCircle(%s, '%s', 0.2, {'x': %d, 'y': %d, 'radius': %d, 'theta_offset': 0, 'theta_extent': (Math.PI*2)});\n",
				canvasId, determineColor(circle), scaled(circle.getOffsetX()), scaled(circle.getOffsetY()), scaled(circle.getRadius())
		);
	}

	@Override
	public String visit(FogOfWarRect rect) {
		return String.format("previewRectangle(%s, '%s', 0.2, { 'x': %d, 'y': %d, 'width': %d, 'height': %d });\n",
				canvasId, determineColor(rect),
				scaled(rect.getOffsetX()), scaled(rect.getOffsetY()), scaled(rect.getWidth()), scaled(rect.getHeight())
		);

	}

	@Override
	public String visit(FogOfWarTriangle triangle) {

		List<XY> poly = triangle.getOrientation()
				.toPolygon(scaled(triangle.getOffsetX()), scaled(triangle.getOffsetY()),
						scaled(triangle.getHorizontalSide()),
						scaled(triangle.getVerticalSide()));

		return String.format("previewPolygon(%s,'%s', 0.2, { points: [%s] });\n", canvasId, determineColor(triangle),
				poly.stream().map(xy -> String.format("{'x': %d, 'y': %d}", xy.getX(), xy.getY())).collect(
						Collectors.joining(", ")));
	}

	private int scaled(int unit) {
		return (int) ((double) unit * factor);
	}

	private String determineColor(FogOfWarShape shape) {
		FogOfWarGroup group = shape.getGroup();

		if (group != null) {
			return group.getVisibilities().stream().filter(v -> v.getView().equals(view)).findAny().map(this::determineColorOfVisibility).orElse(COLOR_INVISIBLE);
		}

		return shape.getVisibilities().stream().filter(v -> v.getView().equals(view)).findAny().map(this::determineColorOfVisibility).orElse(COLOR_INVISIBLE);
	}

	private String determineColorOfVisibility(FogOfWarVisibility v) {
		switch (v.getStatus()) {
			case INVISIBLE:
				return COLOR_INVISIBLE;
			case DM_ONLY:
				return COLOR_DM_ONLY;
			case VISIBLE:
				return COLOR_VISIBLE;
		}

		return COLOR_INVISIBLE;
	}
}
