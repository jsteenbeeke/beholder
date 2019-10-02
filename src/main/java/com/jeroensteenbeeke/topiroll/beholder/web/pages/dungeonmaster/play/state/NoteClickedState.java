package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.play.state;

import com.jeroensteenbeeke.topiroll.beholder.entities.DungeonMasterNote;

public class NoteClickedState extends EntityClickedState<DungeonMasterNote> {
	private static final long serialVersionUID = 1L;

	NoteClickedState(DungeonMasterNote note) {
		super(note);
	}

	@Override
	public <T> T visit(IMapViewStateVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
