package com.jeroensteenbeeke.topiroll.beholder.beans.impl.maprenderer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.jeroensteenbeeke.hyperion.util.Randomizer;
import com.jeroensteenbeeke.topiroll.beholder.beans.IMapRenderer;
import com.jeroensteenbeeke.topiroll.beholder.beans.URLService;
import com.jeroensteenbeeke.topiroll.beholder.dao.TokenInstanceDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.TokenInstanceFilter;
import com.jeroensteenbeeke.topiroll.beholder.util.Calculations;
import com.jeroensteenbeeke.topiroll.beholder.util.JSBuilder;
import com.jeroensteenbeeke.topiroll.beholder.util.JavaScriptHandler;

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
		TokenInstanceFilter filter = new TokenInstanceFilter();
		filter.view().set(mapView);

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
		
		List<TokenInstance> tokens = tokenDAO.findByFilter(filter);

		for (TokenInstance token : tokens) {
			js.__("var imageObj = new Image();");
			js = js.objFunction("imageObj.onload");

			
			

			
			
			final int diameter = token.getDefinition().getDiameterInSquares();
			final long pixels = Calculations.oneInchSquareInPixels(mapView.toResolution(), mapView.getScreenDiagonalInInches());
			final int radius = (int) ((diameter * pixels) / 2);
			
			js.__("context.save();");
			js.__("context.beginPath();");

			js.__("context.moveTo(%d, %d);", 
					rel(token.getOffsetX(), ratio),
					rel(token.getOffsetY(), ratio));
			js.__("context.arc(%d, %d, %d, 0, 2 * Math.PI);", 
					rel(token.getOffsetX() + radius, ratio),
					rel(token.getOffsetY() + radius, ratio),
					rel(radius, ratio));
			
			js.__("context.closePath();");
			js.__("context.clip();");

			
			int x = (int) (token.getOffsetX() * ratio);
			int y = (int) (token.getOffsetY() * ratio);
			int wh = (int) (diameter * pixels * ratio);
			
			js.__("context.drawImage(imageObj, %d, %d, %d, %d);",
					x, y, wh, wh);

			js.__("context.restore();");
			
			js.__("context.beginPath();");

			js.__("context.moveTo(%d, %d);", 
					rel(token.getOffsetX(), ratio),
					rel(token.getOffsetY(), ratio));
			js.__("context.arc(%d, %d, %d, 0, 2 * Math.PI);", 
					rel(token.getOffsetX() + radius, ratio),
					rel(token.getOffsetY() + radius, ratio),
					rel(radius, ratio));
			
			js.__("context.lineWidth = 5;");
			js.__("context.stroke();");

			
			js.__("context.drawImage(imageObj, %d, %d);",
					x, y);

			js.__("context.restore();");
			
			js = js.close();

			js.__("imageObj.src = '%s';",
					urlService.contextRelative(String.format(
							"/tokens/%s/%d/%d?%s", Randomizer.random(44),
							mapView.getId(), token.getId(),
							previewMode ? "preview=true&" : "")));
		}

		js.__("renderState.set('tokens', '%s');", state);
		js = js.close();

		handler.execute(js.toString());
	}
	
	protected final int rel(int input, double multiplier) {
		return (int) (input * multiplier);
	}

}
