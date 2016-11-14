package com.jeroensteenbeeke.topiroll.beholder.beans.impl;

import javax.inject.Inject;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.ScaledMapDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;

	@Component
	@Scope(value="request")  
class MapServiceImpl implements MapService {
		@Inject
		private ScaledMapDAO mapDAO;

		@Override
		public ScaledMap createMap(String name, int squareSize, byte[] data) {
			ScaledMap map = new ScaledMap();
			map.setData(data);
			map.setName(name);
			map.setSquareSize(squareSize);
			mapDAO.save(map);
			
			return map;
		}

}
