/**
 * This file is part of Beholder
 * (C) 2016-2019 Jeroen Steenbeeke
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
package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster;

import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarCircle;
import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarRect;
import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarTriangle;
import com.jeroensteenbeeke.topiroll.beholder.entities.visitor.FogOfWarShapeVisitor;
import com.jeroensteenbeeke.topiroll.beholder.web.data.shapes.XY;

import java.util.List;
import java.util.stream.Collectors;

public class FogOfWarPreviewRenderer implements FogOfWarShapeVisitor<String> {
	private static final long serialVersionUID = 6116200149680201826L;
	private final String canvasId;

	private final double factor;

	public FogOfWarPreviewRenderer(String canvasId, double factor) {
		this.canvasId = canvasId;
		this.factor = factor;
	}

	@Override
	public String visit(FogOfWarCircle circle) {
		return String.format("previewCircle(%s, '#0000ff', 0.2, {'x': %d, 'y': %d, 'radius': %d, 'theta_offset': 0, 'theta_extent': (Math.PI*2)});\n",
				canvasId, scaled(circle.getOffsetX()), scaled(circle.getOffsetY()), scaled(circle.getRadius())
		);
	}

	@Override
	public String visit(FogOfWarRect rect) {
		return String.format("previewRectangle(%s, '#0000ff', 0.2, { 'x': %d, 'y': %d, 'width': %d, 'height': %d });\n",
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

		return String.format("previewPolygon(%s, '#0000ff', 0.2, { points: [%s] });\n", canvasId,
				poly.stream().map(xy -> String.format("{'x': %d, 'y': %d}", xy.getX(), xy.getY())).collect(
						Collectors.joining(", ")));
	}
}
