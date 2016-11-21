package com.jeroensteenbeeke.topiroll.beholder.beans.impl.maprenderer;

import javax.annotation.Nonnull;

import org.apache.wicket.request.UrlUtils;
import org.apache.wicket.request.cycle.RequestCycle;
import org.springframework.stereotype.Component;

import com.jeroensteenbeeke.topiroll.beholder.beans.IMapRenderer;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.util.JavaScriptHandler;

@Component
public class FloorPlanRenderer implements IMapRenderer {

	@Override
	public int getPriority() {
		return Integer.MIN_VALUE;
	}

	@Override
	public void onRefresh(@Nonnull String canvasId,
			@Nonnull JavaScriptHandler handler, @Nonnull MapView mapView) {

		StringBuilder builder = new StringBuilder();
		builder.append("var canvas = document.getElementById('")
				.append(canvasId).append("');\n");

		builder.append("var context = canvas.getContext('2d');");
		builder.append("var imageObj = new Image();\n");

		builder.append("imageObj.onload = function() {\n");
		builder.append("\tcontext.drawImage(imageObj, 0, 0);\n");
		builder.append("};\n");
		builder.append("imageObj.src = '")
				.append(UrlUtils.rewriteToContextRelative(String.format("/maps/%d", mapView.getId()), RequestCycle.get())).append("';\n");

		handler.execute(builder.toString());
	}

}
