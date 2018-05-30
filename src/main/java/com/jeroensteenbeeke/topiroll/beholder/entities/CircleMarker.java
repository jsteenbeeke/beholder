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

import javax.persistence.Entity;

import com.jeroensteenbeeke.topiroll.beholder.entities.visitor.AreaMarkerVisitor;
import org.apache.wicket.markup.html.panel.Panel;

import com.jeroensteenbeeke.topiroll.beholder.web.components.mapcontrol.markers.CircleMarkerController;
import com.jeroensteenbeeke.topiroll.beholder.web.data.shapes.JSCircle;
import com.jeroensteenbeeke.topiroll.beholder.web.data.shapes.JSShape;

@Entity
public class CircleMarker extends AreaMarker {

	private static final long serialVersionUID = 1L;

	
	@Override
	public Panel createPanel(String id) {

		return new CircleMarkerController(id, this);
	}

	@Override
	public <R> R visit(AreaMarkerVisitor<R> visitor) {
		return visitor.visit(this);
	}

	@Override
	public JSShape getShape(double factor, int squareSize) {
		JSCircle circle = new JSCircle();
		int radius = (int) (getExtent() * factor * squareSize / 5);

		circle.setRadius(radius);
		// Treat offset as center instead
		circle.setX((int) (getOffsetX()*factor));
		circle.setY((int) (getOffsetY()*factor));
		circle.setThetaOffset(0.0);
		circle.setThetaExtent(Math.PI*2);

		return circle;
	}
}
