package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.play.state;

import com.jeroensteenbeeke.topiroll.beholder.entities.AreaMarker;
import com.jeroensteenbeeke.topiroll.beholder.entities.DungeonMasterNote;
import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeParticipant;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;
import org.apache.wicket.model.IDetachable;

import java.awt.*;

public interface IMapViewState extends IDetachable {
	default IMapViewState onLocationClicked(Point clickedLocation) {
		return new LocationClickedState(clickedLocation, null);
	}

	default IMapViewState onTokenClicked(TokenInstance token) {
		return new TokenInstanceClickedState(token);
	}

	default IMapViewState onParticipantClicked(InitiativeParticipant participant) {
		return new ParticipantClickedState(participant);
	}

	default IMapViewState onNoteClicked(DungeonMasterNote note) {
		return new NoteClickedState(note);
	}

	default IMapViewState onAreaMarkerClicked(AreaMarker marker) {
		return new AreaMarkerClickedState(marker);
	}

	<T> T visit(IMapViewStateVisitor<T> visitor);
}
