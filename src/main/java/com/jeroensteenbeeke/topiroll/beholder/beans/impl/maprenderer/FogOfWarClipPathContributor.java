/**
 * This file is part of Beholder
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jeroensteenbeeke.topiroll.beholder.beans.impl.maprenderer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jeroensteenbeeke.topiroll.beholder.beans.IClipPathContributor;
import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarShapeDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarShape.JSRenderContext;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.FogOfWarShapeFilter;
import com.jeroensteenbeeke.topiroll.beholder.util.JSBuilder;

@Component
public class FogOfWarClipPathContributor implements IClipPathContributor {
	@Autowired
	private FogOfWarShapeDAO shapeDAO;

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public void contribute(JSBuilder builder, String contextVariable,
			MapView mapView, boolean previewMode) {
		ScaledMap map = mapView.getSelectedMap();

		if (map != null) {
			double multiplier = determineMultiplier(mapView, previewMode, map);

			FogOfWarShapeFilter filter = new FogOfWarShapeFilter();
			filter.map().set(map);

			shapeDAO.findByFilter(filter)
					.forEach(s -> s.renderTo(
							new JSRenderContext(builder, contextVariable,
									multiplier, previewMode, mapView)));

		}

	}

	private double determineMultiplier(MapView mapView, boolean previewMode,
			ScaledMap map) {
		return previewMode ? map.getPreviewFactor() : map.getDisplayFactor(mapView);
	}

}
