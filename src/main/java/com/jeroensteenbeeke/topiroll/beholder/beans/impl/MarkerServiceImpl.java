/**
 * This file is part of Beholder
 * (C) 2016 Jeroen Steenbeeke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.jeroensteenbeeke.topiroll.beholder.beans.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.beans.MarkerService;
import com.jeroensteenbeeke.topiroll.beholder.dao.AreaMarkerDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.*;

import javax.annotation.Nonnull;

@Component
@Scope(value = "request")
class MarkerServiceImpl implements MarkerService {
	@Autowired
	private AreaMarkerDAO markerDAO;
	
	@Autowired
	private MapService mapService;

	@Override
	public void update(CircleMarker marker, String color, int x, int y,
			int radius) {
		marker.setColor(color);
		marker.setOffsetX(x);
		marker.setOffsetY(y);
		marker.setExtent(radius);
		markerDAO.update(marker);
		
		mapService.refreshView(marker.getView());
	}

	@Override
	public void update(ConeMarker marker, String color, int x, int y,
			int radius, int theta) {
		marker.setColor(color);
		marker.setOffsetX(x);
		marker.setOffsetY(y);
		marker.setExtent(radius);
		marker.setTheta(theta);
		markerDAO.update(marker);
		
		mapService.refreshView(marker.getView());
	}

	@Override
	public void update(CubeMarker marker, String color, int x, int y,
			int extent) {
		marker.setColor(color);
		marker.setOffsetX(x);
		marker.setOffsetY(y);
		marker.setExtent(extent);
		markerDAO.update(marker);
		
		mapService.refreshView(marker.getView());

	}

	@Override
	public void update(LineMarker marker, String color, int x, int y,
			int extent, int theta) {
		marker.setColor(color);
		marker.setOffsetX(x);
		marker.setOffsetY(y);
		marker.setExtent(extent);
		marker.setTheta(theta);
		markerDAO.update(marker);
		
		mapService.refreshView(marker.getView());

	}

	@Override
	public void createCircle(@Nonnull MapView view, @Nonnull String color,
			int x, int y, int radius) {

		CircleMarker marker = new CircleMarker();
		marker.setColor(color);
		marker.setExtent(radius);
		marker.setOffsetX(x);
		marker.setOffsetY(y);
		marker.setView(view);
		markerDAO.save(marker);
		
		mapService.refreshView(marker.getView());


	}

	@Override
	public void createCone(@Nonnull MapView view, @Nonnull String color, int x,
			int y, int radius, int theta) {

		ConeMarker marker = new ConeMarker();
		marker.setColor(color);
		marker.setExtent(radius);
		marker.setTheta(theta);
		marker.setOffsetX(x);
		marker.setOffsetY(y);
		marker.setView(view);
		markerDAO.save(marker);
		
		mapService.refreshView(marker.getView());

	}

	@Override
	public void createCube(@Nonnull MapView view, @Nonnull String color, int x,
			int y, int extent) {

		CubeMarker marker = new CubeMarker();
		marker.setExtent(extent);
		marker.setColor(color);
		marker.setOffsetX(x);
		marker.setOffsetY(y);
		marker.setView(view);
		markerDAO.save(marker);
		
		mapService.refreshView(marker.getView());
	}

	@Override
	public void createLine(@Nonnull MapView view, @Nonnull String color, int x,
			int y, int extent, int theta) {

		LineMarker marker = new LineMarker();
		marker.setColor(color);
		marker.setExtent(extent);
		marker.setTheta(theta);
		marker.setOffsetX(x);
		marker.setOffsetY(y);
		marker.setView(view);
		markerDAO.save(marker);
		
		mapService.refreshView(marker.getView());

	}

}
