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

import com.jeroensteenbeeke.topiroll.beholder.web.components.mapcontrol.markers.CubeMarkerController;
import com.jeroensteenbeeke.topiroll.beholder.web.data.shapes.JSRect;
import com.jeroensteenbeeke.topiroll.beholder.web.data.shapes.JSShape;

@Entity
public class CubeMarker extends AreaMarker {

	private static final long serialVersionUID = 1L;

	@Override
	public Panel createPanel(String id) {
		return new CubeMarkerController(id, this);
	}


	@Override
	public JSShape getShape(double factor, int squareSize) {
		JSRect rect = new JSRect();
		final int hw = (int) (getExtent() * factor * squareSize / 5);
		rect.setHeight(hw);
		rect.setWidth(hw);

		// Note: while this property is called offset, we treat it as the center of the cube
		rect.setX((int) (getOffsetX() * factor - (hw/2)));
		rect.setY((int) (getOffsetY() * factor - (hw/2)));

		return rect;
	}
}
