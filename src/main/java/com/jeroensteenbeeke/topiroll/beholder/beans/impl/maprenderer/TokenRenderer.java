package com.jeroensteenbeeke.topiroll.beholder.beans.impl.maprenderer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.jeroensteenbeeke.hyperion.util.ImageUtil;
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
		
		double ratio = Calculations.scale(mapView.getSelectedMap().getSquareSize())
				.toResolution(mapView.toResolution())
				.onScreenWithDiagonalSize(
						mapView.getScreenDiagonalInInches());

		
		if (previewMode) {
			

			int width = (int) ImageUtil.getImageDimensions(map.getData())
					.getWidth();

			while (width > 640) {
				width = (int) (width * 0.9);
				ratio = ratio * 0.9;
			}
		}
		
		List<TokenInstance> tokens = tokenDAO.findByFilter(filter);

		for (TokenInstance token : tokens) {
			js.__("var imageObj = new Image();");
			js = js.objFunction("imageObj.onload");

			
			

			js.__("context.drawImage(imageObj, %d, %d);",
					(int) (token.getOffsetX() * ratio), (int) (token.getOffsetY() * ratio));

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

}
