package com.jeroensteenbeeke.topiroll.beholder.beans.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jeroensteenbeeke.hyperion.util.TypedActionResult;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarGroupDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarShapeDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.MapViewDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.ScaledMapDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.*;

@Component
@Scope(value = "request")
class MapServiceImpl implements MapService {
	@Autowired
	private ScaledMapDAO mapDAO;

	@Autowired
	private MapViewDAO viewDAO;

	@Autowired
	private FogOfWarShapeDAO shapeDAO;
	
	@Autowired
	private FogOfWarGroupDAO groupDAO;

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
	
	@Override
	public TypedActionResult<FogOfWarGroup> createGroup(ScaledMap map,
			String name, List<FogOfWarShape> shapes) {
		if (shapes.isEmpty()) {
			return TypedActionResult.fail("No shapes selected");
		}
		
		FogOfWarGroup group = new FogOfWarGroup();
		group.setMap(map);
		group.setName(name);
		group.setStatus(VisibilityStatus.INVISIBLE);
		groupDAO.save(group);
		
		shapes.forEach(shape -> {
			shape.setGroup(group);
			shapeDAO.update(shape);
		});
		
		return TypedActionResult.ok(group);
	}

}
