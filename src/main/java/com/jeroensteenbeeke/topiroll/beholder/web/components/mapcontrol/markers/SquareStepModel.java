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
