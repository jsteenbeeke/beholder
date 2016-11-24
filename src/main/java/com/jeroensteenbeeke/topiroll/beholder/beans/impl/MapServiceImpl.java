package com.jeroensteenbeeke.topiroll.beholder.beans.impl;

import javax.inject.Inject;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarShapeDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.MapViewDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.ScaledMapDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarCircle;
import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarRect;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;

@Component
@Scope(value = "request")
class MapServiceImpl implements MapService {
	@Inject
	private ScaledMapDAO mapDAO;

	@Inject
	private MapViewDAO viewDAO;

	@Inject
	private FogOfWarShapeDAO shapeDAO;

	@Override
	public ScaledMap createMap(BeholderUser user, String name, int squareSize,
			byte[] data) {
		ScaledMap map = new ScaledMap();
		map.setData(data);
		map.setName(name);
		map.setSquareSize(squareSize);
		map.setOwner(user);
		mapDAO.save(map);

		return map;
	}

	@Override
	public void selectMap(MapView view, ScaledMap map) {
		view.setSelectedMap(map);
		viewDAO.update(view);
	}

	@Override
	public void unselectMap(MapView view) {
		view.setSelectedMap(null);
		viewDAO.update(view);
	}

	@Override
	public void addFogOfWarCircle(ScaledMap map, int radius, int offsetX,
			int offsetY) {
		FogOfWarCircle circle = new FogOfWarCircle();
		circle.setMap(map);
		circle.setOffsetX(offsetX);
		circle.setOffsetY(offsetY);
		circle.setRadius(radius);
		shapeDAO.save(circle);

	}

	@Override
	public void addFogOfWarRect(ScaledMap map, int width, int height,
			int offsetX, int offsetY) {
		FogOfWarRect rect = new FogOfWarRect();
		rect.setMap(map);
		rect.setHeight(height);
		rect.setWidth(width);
		rect.setOffsetX(offsetX);
		rect.setOffsetY(offsetY);
		shapeDAO.save(rect);
	}

}
