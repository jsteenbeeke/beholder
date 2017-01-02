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

package com.jeroensteenbeeke.topiroll.beholder.web.components.mapcontrol.markers;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import com.jeroensteenbeeke.topiroll.beholder.entities.AreaMarker;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;

public class SquareStepModel extends LoadableDetachableModel<Integer> {
	private static final long serialVersionUID = 1L;
	
	private IModel<? extends AreaMarker> markerModel;
	
	

	SquareStepModel(IModel<? extends AreaMarker> markerModel) {
		this.markerModel = markerModel;
	}



	@Override
	protected Integer load() {
		AreaMarker marker = markerModel.getObject();
		
		MapView view = marker.getView();
		ScaledMap map = view.getSelectedMap();
		
		if (map != null) {
			return map.getSquareSize() / 4;
		}
		
		return 1;
	}

	
	@Override
	protected void onDetach() {
		super.onDetach();
		markerModel.detach();
	}
}
