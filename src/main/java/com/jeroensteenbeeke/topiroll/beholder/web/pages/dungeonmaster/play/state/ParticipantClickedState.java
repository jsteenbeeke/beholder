package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.play.state;

import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeParticipant;

public class ParticipantClickedState extends EntityClickedState<InitiativeParticipant> {
	private static final long serialVersionUID = 1L;

	ParticipantClickedState(InitiativeParticipant participant) {
		super(participant);
	}

	@Override
	public <T> T visit(IMapViewStateVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
