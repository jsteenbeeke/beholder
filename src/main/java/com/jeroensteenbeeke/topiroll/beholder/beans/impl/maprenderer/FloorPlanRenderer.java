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


		JSBuilder js = JSBuilder.create();
		js.__("var canvas = document.getElementById('%s');", canvasId);
		js = js.ifBlock("canvas");
		js = js.ifBlock("renderState.check('floorplan', '%s') === false", state);
		js.__("var context = canvas.getContext('2d');");
		js.__("var imageObj = new Image();");
		js = js.objFunction("imageObj.onload");
		js.__("canvas.width = %d;", w);
		js.__("canvas.height = %d;", h);

		if (clipPaths.isSatisfied()) {
			js.__("context.save();");
			js.__("context.beginPath();");

			for (IClipPathContributor contributor : clipPaths.all()) {
				contributor.contribute(js, "context", mapView, previewMode);
			}
			js.__("context.closePath();");
			js.__("context.clip();");

		}

		
		js.__("context.drawImage(imageObj, 0, 0, %d, %d);", w, h);

		if (clipPaths.isSatisfied()) {
			js.__("context.restore();");
		}

		js.__("renderState.set('floorplan', '%s');", state);
		js = js.close();
		js.__("imageObj.src = '%s';",
				urlService.contextRelative(String.format("/maps/%s/%d?%s",
						Randomizer.random(44), mapView.getId(),
						previewMode ? "preview=true&" : "")));

		handler.execute(js.toString());
	}

}
