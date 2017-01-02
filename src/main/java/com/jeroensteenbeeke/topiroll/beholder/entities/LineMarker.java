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

import org.apache.wicket.markup.html.panel.Panel;

import com.jeroensteenbeeke.topiroll.beholder.util.JSBuilder;
import com.jeroensteenbeeke.topiroll.beholder.web.components.mapcontrol.markers.LineMarkerController;

@Entity
public class LineMarker extends AreaMarker {

	private static final long serialVersionUID = 1L;

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
	public void renderTo(String contextVariable, JSBuilder js, double ratio,
			int squareSize) {
		int x = (int) (ratio * getOffsetX());
		int y = (int) (ratio * getOffsetY());
		int extent = (int) (ratio * getExtent() * squareSize / 5);
		double theta = Math.toRadians((double) getTheta());

		int h = (int) (extent * Math.sin(theta));
		int w = (int) (extent * Math.cos(theta));

		js.__("%s.save();", contextVariable);
		js.__("%s.moveTo(%d, %d);", contextVariable, x, y);
		js.__("%s.lineTo(%d, %d);", contextVariable, (x + w), (y + h));
		js.__("%s.closePath()", contextVariable);
		js.__("%s.strokeStyle = '#%s';", contextVariable, getColor());
		js.__("%s.strokeWidth = %f;", contextVariable, Math.max(2.0f, 4.0*ratio));
		js.__("%s.stroke();", contextVariable);
		js.__("%s.restore();", contextVariable);
		js.__("%s.strokeWidth = 0;", contextVariable);
		js.__("%s.strokeStyle = '#000000';", contextVariable);
		
	}

	@Override
	public Panel createPanel(String id) {

		return new LineMarkerController(id, this);
	}
	
	@Override
	public String getMarkerState() {
		return ";CONE;theta=".concat(Integer.toString(getTheta()));
	}
}
