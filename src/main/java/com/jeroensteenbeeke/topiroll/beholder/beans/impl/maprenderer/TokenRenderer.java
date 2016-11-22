package com.jeroensteenbeeke.topiroll.beholder.beans.impl.maprenderer;

import com.jeroensteenbeeke.topiroll.beholder.beans.IMapRenderer;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.util.JavaScriptHandler;

public class TokenRenderer implements IMapRenderer {

	@Override
	public int getPriority() {
		return 5;
	}

	@Override
	public void onRefresh(String canvasId, JavaScriptHandler handler,
			MapView mapView, boolean previewMode) {

	}

}
