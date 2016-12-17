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

import java.awt.Dimension;

import javax.annotation.Nonnull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jeroensteenbeeke.hyperion.solstice.api.Any;
import com.jeroensteenbeeke.hyperion.util.Randomizer;
import com.jeroensteenbeeke.topiroll.beholder.beans.IClipPathContributor;
import com.jeroensteenbeeke.topiroll.beholder.beans.IMapRenderer;
import com.jeroensteenbeeke.topiroll.beholder.beans.URLService;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.util.JSBuilder;
import com.jeroensteenbeeke.topiroll.beholder.util.JavaScriptHandler;

@Component
public class FloorPlanRenderer implements IMapRenderer {

	@Autowired
	private URLService urlService;

	@Autowired
	private Any<IClipPathContributor> clipPaths;

	@Override
	public int getPriority() {
		return Integer.MIN_VALUE;
	}

	@Override
	public void onRefresh(@Nonnull String canvasId,
			@Nonnull JavaScriptHandler handler, @Nonnull MapView mapView,
			boolean previewMode) {
		final String state = mapView.calculateState();
		
		ScaledMap map = mapView.getSelectedMap();
		Dimension displayDimension = new Dimension(1, 1);
		
		if (map != null) {
			if (previewMode) {
				displayDimension = map.getPreviewDimension();
			} else {
				displayDimension = map.getDisplayDimension(mapView);
			}
		}
		
		int w = (int) displayDimension.getWidth();
		int h = (int) displayDimension.getHeight();

		String imageVar = String.format("imgObj%s", Randomizer.random(15));
		
		JSBuilder js = JSBuilder.create();
		js.__("var canvas = document.getElementById('%s');", canvasId);
		
		js = js.ifBlock("canvas");
		js = js.ifBlock("renderState.check('floorplan', '%s') === false", state);
		js.__("canvas.width = %d;", w);
		js.__("canvas.height = %d;", h);
		js.__("var context = canvas.getContext('2d');");
		js.__("var %s = new Image();", imageVar);
		js = js.objFunction(String.format("%s.onload", imageVar));
		

		if (clipPaths.isSatisfied()) {
			
			js.__("context.save();");
			js.__("context.beginPath();");

			for (IClipPathContributor contributor : clipPaths.all()) {
				contributor.contribute(js, "context", mapView, previewMode);
			}
			js.__("context.closePath();");
			js.__("context.clip();");

		}

		js = js.tryBlockWithConsoleLog();
		js.__("context.drawImage(%s, 0, 0, %d, %d);", imageVar, w, h);
		js = js.close();

		if (clipPaths.isSatisfied()) {
			js.__("context.restore();");
		}

		js.__("renderState.set('floorplan', '%s');", state);
		js = js.close();
		js.__("%s.src = '%s';", imageVar,
				urlService.contextRelative(String.format("/maps/%s/%d?%s",
						Randomizer.random(44), mapView.getId(),
						previewMode ? "preview=true&" : "")));

		handler.execute(js.toString());
	}

}
