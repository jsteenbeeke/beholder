package com.jeroensteenbeeke.topiroll.beholder.beans.impl.maprenderer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.jeroensteenbeeke.topiroll.beholder.beans.IMapRenderer;
import com.jeroensteenbeeke.topiroll.beholder.dao.TokenInstanceDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.TokenInstanceFilter;
import com.jeroensteenbeeke.topiroll.beholder.util.JavaScriptHandler;

public class TokenRenderer implements IMapRenderer {

	@Autowired
	private TokenInstanceDAO tokenDAO;
	
	@Override
	public int getPriority() {
		return 5;
	}

	@Override
	public void onRefresh(String canvasId, JavaScriptHandler handler,
			MapView mapView, boolean previewMode) {
		TokenInstanceFilter filter = new TokenInstanceFilter();
		filter.view().set(mapView);
		
		List<TokenInstance> tokens = tokenDAO.findByFilter(filter);
		
		for (TokenInstance token : tokens) {
			
		}
	}

}
