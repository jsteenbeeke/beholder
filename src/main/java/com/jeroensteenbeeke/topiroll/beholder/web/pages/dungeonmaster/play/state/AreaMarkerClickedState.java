package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.play.state;

import com.jeroensteenbeeke.topiroll.beholder.entities.AreaMarker;

public class AreaMarkerClickedState extends EntityClickedState<AreaMarker> {
	private static final long serialVersionUID = 1L;

	AreaMarkerClickedState(AreaMarker marker) {
		super(marker);
	}

	@Override
	public <T> T visit(IMapViewStateVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
