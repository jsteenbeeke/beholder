package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.play.state;

public interface IMapViewStateVisitor<T> {
	T visit(NoteClickedState noteClickedState);

	T visit(ParticipantClickedState participantClickedState);

	T visit(TokenInstanceClickedState tokenInstanceClickedState);

	T visit(LocationClickedState locationClickedState);

	T visit(EmptyState emptyState);

	T visit(AreaMarkerClickedState areaMarkerClickedState);
}
