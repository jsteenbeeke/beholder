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
/**
 *     This file is part of Beholder
 *     (C) 2016 Jeroen Steenbeeke
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jeroensteenbeeke.topiroll.beholder.beans.impl.maprenderer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jeroensteenbeeke.topiroll.beholder.beans.IMapRenderer;
import com.jeroensteenbeeke.topiroll.beholder.dao.AreaMarkerDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.AreaMarker;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.AreaMarkerFilter;
import com.jeroensteenbeeke.topiroll.beholder.util.JSBuilder;
import com.jeroensteenbeeke.topiroll.beholder.util.JavaScriptHandler;

@Component
public class AreaMarkerRenderer implements IMapRenderer {
	
	@Autowired
	private AreaMarkerDAO markerDAO;

	@Override
	public int getPriority() {
		return 3;
	}

	@Override
	public void onRefresh(String canvasId, JavaScriptHandler handler,
			MapView mapView, boolean previewMode) {
		final String state = mapView.calculateState();

		JSBuilder js = JSBuilder.create();
		js.__("var canvas = document.getElementById('%s');", canvasId);
		js = js.ifBlock("canvas");
		js = js.ifBlock("!renderState.check('markers', '%s')", state);
		js = js.objFunction("var renderMarkers");
		js.__("var context = canvas.getContext('2d');");

		ScaledMap map = mapView.getSelectedMap();

		double ratio = 1.0;
		
		int squareSize = 1;

		if (map != null) {
			if (previewMode) {
				ratio = map.getPreviewFactor();
			} else {
				ratio = map.getDisplayFactor(mapView);
			}
			
			squareSize = map.getSquareSize();
		}

		AreaMarkerFilter filter = new AreaMarkerFilter();
		filter.view().set(mapView);
		
		for (AreaMarker marker: markerDAO.findByFilter(filter)) {
			marker.renderTo("context", js, ratio, squareSize);
		}
		
		js.__("renderState.set('markers', '%s');", state);
		js = js.close();
		js.__("onAfterRenderFloorplan.push(renderMarkers);");
		
		handler.execute(js.toString());

	}

}
