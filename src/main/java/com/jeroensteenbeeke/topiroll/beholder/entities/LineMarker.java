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

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.sound.sampled.Line;

import com.jeroensteenbeeke.topiroll.beholder.entities.visitor.AreaMarkerVisitor;
import org.apache.wicket.markup.html.panel.Panel;

import com.jeroensteenbeeke.topiroll.beholder.web.components.mapcontrol.markers.LineMarkerController;
import com.jeroensteenbeeke.topiroll.beholder.web.data.shapes.JSCircle;
import com.jeroensteenbeeke.topiroll.beholder.web.data.shapes.JSShape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
public class LineMarker extends AreaMarker {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(LineMarker.class);

	public static final int LINE_ANGLE = 3;
	private static final int LINE_MARKER_CUTOFF = 15;

	@Column(nullable = false)
	private int theta;

	@Nonnull
	public int getTheta() {
		return theta;
	}

	public void setTheta(@Nonnull int theta) {
		this.theta = theta;
	}

	
	@Override
	public Panel createPanel(String id) {

		return new LineMarkerController(id, this);
	}

	@Override
	public <R> R visit(AreaMarkerVisitor<R> visitor) {
		return visitor.visit(this);
	}

	@Override
	public JSShape getShape(double factor, int squareSize) {
		JSCircle circle = new JSCircle();
		circle.setRadius((int) (factor * (getExtent() * squareSize / 5)));
		circle.setX((int) (getOffsetX() * factor));
		circle.setY((int) (getOffsetY() * factor));

		double angle = LINE_ANGLE;

		long ext = Math.round(circle.getRadius());

		while (ext > 50) {
			angle = angle * 0.9;
			ext = ext / 2;
		}

		circle.setThetaOffset(Math.toRadians((double) getTheta()-(angle/2.0)));
		circle.setThetaExtent(Math.toRadians(angle));

		log.info("Line r {} \u03b8 {} {}", circle.getRadius(), circle.getThetaOffset(), circle.getThetaExtent());

		return circle;
	}
}
