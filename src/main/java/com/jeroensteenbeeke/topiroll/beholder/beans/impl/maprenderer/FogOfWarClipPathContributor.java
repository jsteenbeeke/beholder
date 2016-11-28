package com.jeroensteenbeeke.topiroll.beholder.beans.impl.maprenderer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jeroensteenbeeke.hyperion.util.ImageUtil;
import com.jeroensteenbeeke.topiroll.beholder.beans.IClipPathContributor;
import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarShapeDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.FogOfWarShapeFilter;
import com.jeroensteenbeeke.topiroll.beholder.util.Calculations;
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

			shapeDAO.findByFilter(filter).forEach(
					s -> s.renderTo(builder, contextVariable, multiplier, previewMode));

		}

	}

	private double determineMultiplier(MapView mapView, boolean previewMode,
			ScaledMap map) {
		double multiplier = previewMode ? 1.0 : Calculations.scale(map.getSquareSize()).toResolution(mapView.toResolution()).onScreenWithDiagonalSize(mapView.getScreenDiagonalInInches());
		
		if (previewMode) {
			int width = (int) ImageUtil.getImageDimensions(map.getData()).getWidth();
			while (width > 640) {
				width = (int) (width * 0.9);
				multiplier = multiplier * 0.9;
			}
		}
		return multiplier;
	}

}
