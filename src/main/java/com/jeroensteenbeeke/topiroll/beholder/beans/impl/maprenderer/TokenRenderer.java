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

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jeroensteenbeeke.topiroll.beholder.beans.IMapRenderer;
import com.jeroensteenbeeke.topiroll.beholder.beans.URLService;
import com.jeroensteenbeeke.topiroll.beholder.dao.TokenInstanceDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.TokenInstanceFilter;
import com.jeroensteenbeeke.topiroll.beholder.util.JSBuilder;
import com.jeroensteenbeeke.topiroll.beholder.util.JavaScriptHandler;

@Component
public class TokenRenderer implements IMapRenderer {

	@Autowired
	private TokenInstanceDAO tokenDAO;

	@Autowired
	private URLService urlService;

	@Override
	public int getPriority() {
		return 5;
	}

	@Override
	public void onRefresh(String canvasId, JavaScriptHandler handler,
			MapView mapView, boolean previewMode) {

		final String state = mapView.calculateState();

		JSBuilder js = JSBuilder.create();
		js.__("var canvas = document.getElementById('%s');", canvasId);
		js = js.ifBlock("canvas");
		js = js.ifBlock("!renderState.check('tokens', '%s')", state);
		js.__("var context = canvas.getContext('2d');");

		ScaledMap map = mapView.getSelectedMap();

		double ratio = 1.0;

		if (map != null) {
			if (previewMode) {
				ratio = map.getPreviewFactor();
			} else {
				ratio = map.getDisplayFactor(mapView);
			}
		}

		TokenInstanceFilter filter = new TokenInstanceFilter();
		if (map != null) {
			filter.map().set(map);
		} else {
			// Map can't be null, so should return empty set
			filter.map().isNull();
		}
		filter.show().set(true);

		List<TokenInstance> tokens = tokenDAO.findByFilter(filter).stream()
				.filter(i -> i.isVisible(mapView, previewMode)).collect(Collectors.toList());

		for (TokenInstance token : tokens) {
			Long tokenId = token.getDefinition().getId();
			js.__("var imageObj%d = new Image();", tokenId);
			js = js.objFunction(String.format("imageObj%d.onload", tokenId));

			final int diameter = token.getDefinition().getDiameterInSquares();
			final long pixels = (long) (map.getSquareSize() * ratio);
			final int radius = (int) ((diameter * pixels) / 2);
			int x = (int) Math.ceil((1+token.getOffsetX()) * ratio );
			int y = (int) Math.ceil((1+token.getOffsetY()) * ratio );
			int wh = (int) (diameter * pixels);

			js.__("context.save();");
			js.__("context.beginPath();");

			js.__("context.moveTo(%d, %d);", x, y);
			js.__("context.arc(%d, %d, %d, 0, 2 * Math.PI);", x + radius,
					y + radius, radius);

			js.__("context.closePath();");
			js.__("context.clip();");

			js.__("context.drawImage(imageObj%d, %d, %d, %d, %d);", tokenId,  x, y, wh, wh);

			js.__("context.restore();");

			js.__("context.beginPath();");

			// js.__("context.moveTo(%d, %d);", rel(token.getOffsetX(), ratio),
			// rel(token.getOffsetY(), ratio));
			js.__("context.arc(%d, %d, %d, 0, 2 * Math.PI);", x + radius,
					y + radius, radius);

			String tokenColor = token.getBorderIntensity()
					.getColor(token.getBorderType());

			js.__("context.lineWidth = %d;", (int) Math.max(1, ratio));
			js.__("context.strokeStyle = '%s';", tokenColor);
			js.__("context.stroke();");

			String badge = token.getBadge();
			if (badge != null && !previewMode) {

				int box_top_y = y + 5 * (wh / 6);
				int box_bottom_y = y + wh;

				int box_height = wh / 6;
				

				int box_width = (int) (badge.length() * 2 * ratio);

				int box_left_x = x + (wh / 2) - box_width; 

				
				int text_y = (3 * box_bottom_y + box_top_y) / 4;

				js.__("context.lineWidth = 1;");
				js.__("context.strokeStyle = '%s';", tokenColor);
				js.__("context.fillStyle = '%s';", "#ffffff");
				js.__("context.moveTo(%d, %d);", box_left_x, y + 3);
				js.__("context.fillRect(%d, %d, %d, %d);", box_left_x, box_top_y,
						box_width, box_height);

				js.__("context.strokeRect(%d, %d, %d, %d);", box_left_x, box_top_y,
						box_width, box_height);

				js.__("context.fillStyle = '%s';", tokenColor);

				js.__("context.fillText('%s', %d, %d)", badge, box_left_x, text_y);

				js.__("context.restore();");
			}

			js = js.close();

			js.__("imageObj%d.src = '%s' + 'context=' + mapViewContext;", tokenId,
					urlService.contextRelative(String.format("/tokens/%d?%s",
							tokenId, previewMode
									? "preview=true&" : "")));
		}

		js.__("renderState.set('tokens', '%s');", state);
		js = js.close();

		handler.execute(js.toString());
	}

	protected final int rel(int input, double multiplier) {
		return (int) (input * multiplier);
	}

}
