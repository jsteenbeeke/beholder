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
package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.play.exploration;

import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarGroupVisibilityDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.FogOfWarGroupVisibilityFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.visitor.FogOfWarShapeVisitor;
import com.jeroensteenbeeke.topiroll.beholder.web.data.shapes.XY;

import java.util.List;
import java.util.stream.Collectors;

public class ExplorationShapeRenderer implements FogOfWarShapeVisitor<String> {
	private static final String COLOR_INVISIBLE = "#ff0000";

	private static final String COLOR_DM_ONLY = "#0000ff";

	private static final String COLOR_VISIBLE = "#00ff00";
	private static final long serialVersionUID = 7202249315005854476L;

	private final String canvasId;

	private final double factor;

	private final MapView view;

	private final FogOfWarGroupVisibilityDAO visibilityDAO;

	public ExplorationShapeRenderer(String canvasId, double factor, MapView view, FogOfWarGroupVisibilityDAO visibilityDAO) {
		this.canvasId = canvasId;
		this.factor = factor;
		this.view = view;
		this.visibilityDAO = visibilityDAO;
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
			FogOfWarGroupVisibilityFilter filter = new FogOfWarGroupVisibilityFilter();
			filter.group(group);
			filter.view(view);

			return visibilityDAO.getUniqueByFilter(filter).map(this::determineColorOfVisibility).getOrElse(() -> COLOR_INVISIBLE);
		}

		return COLOR_INVISIBLE;
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
