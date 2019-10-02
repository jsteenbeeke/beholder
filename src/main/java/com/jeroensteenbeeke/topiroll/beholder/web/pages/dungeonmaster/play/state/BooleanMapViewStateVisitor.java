package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.play.state;

public class BooleanMapViewStateVisitor implements IMapViewStateVisitor<Boolean> {
	@Override
	public Boolean visit(NoteClickedState noteClickedState) {
		return false;
	}

	@Override
	public Boolean visit(ParticipantClickedState participantClickedState) {
		return false;
	}

	@Override
	public Boolean visit(TokenInstanceClickedState tokenInstanceClickedState) {
		return false;
	}

	@Override
	public Boolean visit(LocationClickedState locationClickedState) {
		return false;
	}

	@Override
	public Boolean visit(EmptyState emptyState) {
		return false;
	}

	@Override
	public Boolean visit(AreaMarkerClickedState areaMarkerClickedState) {
		return false;
	}
}
