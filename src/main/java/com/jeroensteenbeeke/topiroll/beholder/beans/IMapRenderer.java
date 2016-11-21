package com.jeroensteenbeeke.topiroll.beholder.beans;

import javax.annotation.Nonnull;

import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.util.JavaScriptHandler;

public interface IMapRenderer {
	/**
	 * Indicates when this renderer should execute relative to other renderes. Executed in ascending order
	 * @return The priority of this renderer
	 */
	int getPriority();
	
	void onRefresh(@Nonnull String canvasId, @Nonnull JavaScriptHandler handler, @Nonnull MapView mapView);
}
