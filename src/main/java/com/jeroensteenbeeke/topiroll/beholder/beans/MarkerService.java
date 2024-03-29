/*
 * This file is part of Beholder
 * Copyright (C) 2016 - 2023 Jeroen Steenbeeke
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
package com.jeroensteenbeeke.topiroll.beholder.beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.beans.MarkerService;
import com.jeroensteenbeeke.topiroll.beholder.dao.AreaMarkerDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.jetbrains.annotations.NotNull;

@Service
@Scope(value = "request")
public class MarkerService {
	@Autowired
	private AreaMarkerDAO markerDAO;
	
	@Autowired
	private MapService mapService;

	@Transactional
	public void update(@NotNull CircleMarker marker, @NotNull String color, int x, int y,
					   int radius) {
		marker.setColor(color);
		marker.setOffsetX(x);
		marker.setOffsetY(y);
		marker.setExtent(radius);
		markerDAO.update(marker);
		
		mapService.refreshView(marker.getView());
	}

	@Transactional
	public void update(@NotNull ConeMarker marker, @NotNull String color, int x, int y,
					   int radius, int theta) {
		marker.setColor(color);
		marker.setOffsetX(x);
		marker.setOffsetY(y);
		marker.setExtent(radius);
		marker.setTheta(theta);
		markerDAO.update(marker);
		
		mapService.refreshView(marker.getView());
	}

	@Transactional
	public void update(@NotNull CubeMarker marker, @NotNull String color, int x, int y,
					   int extent) {
		marker.setColor(color);
		marker.setOffsetX(x);
		marker.setOffsetY(y);
		marker.setExtent(extent);
		markerDAO.update(marker);
		
		mapService.refreshView(marker.getView());

	}

	@Transactional
	public void update(@NotNull LineMarker marker, @NotNull String color, int x, int y,
					   int extent, int theta) {
		marker.setColor(color);
		marker.setOffsetX(x);
		marker.setOffsetY(y);
		marker.setExtent(extent);
		marker.setTheta(theta);
		markerDAO.update(marker);
		
		mapService.refreshView(marker.getView());

	}

	@Transactional
	public void createCircle(@NotNull MapView view, @NotNull String color,
			int x, int y, int radius) {

		CircleMarker marker = new CircleMarker();
		marker.setColor(color);
		marker.setExtent(radius);
		marker.setOffsetX(x);
		marker.setOffsetY(y);
		marker.setView(view);
		markerDAO.save(marker);

		view.getMarkers().add(marker);
		
		mapService.refreshView(marker.getView());


	}

	@Transactional
	public void createCone(@NotNull MapView view, @NotNull String color, int x,
			int y, int radius, int theta) {

		ConeMarker marker = new ConeMarker();
		marker.setColor(color);
		marker.setExtent(radius);
		marker.setTheta(theta);
		marker.setOffsetX(x);
		marker.setOffsetY(y);
		marker.setView(view);
		markerDAO.save(marker);

		view.getMarkers().add(marker);

		mapService.refreshView(marker.getView());

	}

	@Transactional
	public void createCube(@NotNull MapView view, @NotNull String color, int x,
			int y, int extent) {

		CubeMarker marker = new CubeMarker();
		marker.setExtent(extent);
		marker.setColor(color);
		marker.setOffsetX(x);
		marker.setOffsetY(y);
		marker.setView(view);
		markerDAO.save(marker);

		view.getMarkers().add(marker);

		mapService.refreshView(marker.getView());
	}

	@Transactional
	public void createLine(@NotNull MapView view, @NotNull String color, int x,
			int y, int extent, int theta) {

		LineMarker marker = new LineMarker();
		marker.setColor(color);
		marker.setExtent(extent);
		marker.setTheta(theta);
		marker.setOffsetX(x);
		marker.setOffsetY(y);
		marker.setView(view);
		markerDAO.save(marker);

		view.getMarkers().add(marker);

		mapService.refreshView(marker.getView());

	}

}
