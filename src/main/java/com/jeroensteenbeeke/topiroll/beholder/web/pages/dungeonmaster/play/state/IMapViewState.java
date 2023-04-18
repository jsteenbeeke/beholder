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
