package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.play.state;

public class MapViewStateVisitorAdapter<T> implements IMapViewStateVisitor<T> {
	@Override
	public T visit(NoteClickedState noteClickedState) {
		return null;
	}

	@Override
	public T visit(ParticipantClickedState participantClickedState) {
		return null;
	}

	@Override
	public T visit(TokenInstanceClickedState tokenInstanceClickedState) {
		return null;
	}

	@Override
	public T visit(LocationClickedState locationClickedState) {
		return null;
	}

	@Override
	public T visit(EmptyState emptyState) {
		return null;
	}

	@Override
	public T visit(AreaMarkerClickedState areaMarkerClickedState) {
		return null;
	}
}
