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

import org.apache.wicket.markup.html.panel.Panel;

import com.jeroensteenbeeke.topiroll.beholder.util.JSBuilder;
import com.jeroensteenbeeke.topiroll.beholder.web.components.mapcontrol.markers.CubeMarkerController;

	@Entity 
public class CubeMarker extends AreaMarker {



	private static final long serialVersionUID = 1L;

	@Override
		public void renderTo(String contextVariable, JSBuilder js, double ratio,
				int squareSize) {
		int x = (int) (ratio * getOffsetX());
		int y = (int) (ratio * getOffsetY());
		int extent = (int) (ratio * getExtent() * squareSize / 5);
		
		
		js.__("%s.save();", contextVariable);
		js.__("%s.globalAlpha = 0.5;", contextVariable);
		js.__("%s.beginPath();", contextVariable);
		js.__("%s.rect(%d, %d, %d, %d);", contextVariable, x, y, extent, extent);
		js.__("%s.closePath()", contextVariable);
		js.__("%s.fillStyle = '#%s';", contextVariable, getColor());
		js.__("%s.fill();", contextVariable);
		js.__("%s.restore();", contextVariable);
		}

	@Override
	public Panel createPanel(String id) {

		return new CubeMarkerController(id, this);
	}
	
	@Override
	public String getMarkerState() {
		return ";CUBE";
	}
}
