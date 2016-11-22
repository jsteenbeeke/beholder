package com.jeroensteenbeeke.topiroll.beholder.beans.impl.maprenderer;

import com.jeroensteenbeeke.topiroll.beholder.beans.IMapRenderer;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.util.JSBuilder;
import com.jeroensteenbeeke.topiroll.beholder.util.JavaScriptHandler;

public class FogOfWarRenderer implements IMapRenderer {

	@Override
	public int getPriority() {
		return 5;
	}

	@Override
	public void onRefresh(String canvasId, JavaScriptHandler handler,
			MapView mapView, boolean previewMode) {
		ScaledMap map = mapView.getSelectedMap();
		
		if (map != null) {
		
		String state = mapView.calculateState();
		
		JSBuilder js = JSBuilder.create();
		js.__("var canvas = document.getElementById('%s');", canvasId);
		js = js.ifBlock("canvas");
		js = js.ifBlock("!renderState.check('fog', '%s')", state);
		js.__("var context = canvas.getContext('2d');");
		if (previewMode) {
			js.__("context.globalAlpha = 0.5;");
		}
		
		

		handler.execute(js.toString());
		}
	}

}
