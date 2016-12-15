package com.jeroensteenbeeke.topiroll.beholder.beans.impl.maprenderer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jeroensteenbeeke.topiroll.beholder.beans.IClipPathContributor;
import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarShapeDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarShape.JSRenderContext;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.FogOfWarShapeFilter;
import com.jeroensteenbeeke.topiroll.beholder.util.JSBuilder;

@Component
public class FogOfWarClipPathContributor implements IClipPathContributor {
	@Autowired
	private FogOfWarShapeDAO shapeDAO;

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public void contribute(JSBuilder builder, String contextVariable,
			MapView mapView, boolean previewMode) {
		ScaledMap map = mapView.getSelectedMap();

		if (map != null) {
			double multiplier = determineMultiplier(mapView, previewMode, map);

			FogOfWarShapeFilter filter = new FogOfWarShapeFilter();
			filter.map().set(map);

			shapeDAO.findByFilter(filter)
					.forEach(s -> s.renderTo(
							new JSRenderContext(builder, contextVariable,
									multiplier, previewMode, mapView)));

		}

	}

	private double determineMultiplier(MapView mapView, boolean previewMode,
			ScaledMap map) {
		return previewMode ? map.getPreviewFactor() : map.getDisplayFactor(mapView);
	}

}
