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
package com.jeroensteenbeeke.topiroll.beholder.beans;

import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeLocation;
import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeParticipant;
import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeType;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;

public interface InitiativeService {
	void hideInitiative(MapView view);
	
	void showInitiative(MapView view, InitiativeLocation location);
	
	void addInitiative(MapView view, String name, int score, InitiativeType type);

	void reroll(MapView view);
	
	void removeParticipant(InitiativeParticipant participant);

	boolean canMoveUp(InitiativeParticipant participant);

	boolean canMoveDown(InitiativeParticipant participant);

	void moveUp(InitiativeParticipant participant);
	
	void moveDown(InitiativeParticipant participant);
	
	void select(InitiativeParticipant participant);
	
	void selectNext(MapView view);

	void setViewInitiativeMargin(MapView view, Integer margin);
}
