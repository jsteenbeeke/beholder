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

import javax.annotation.Nonnull;

public interface InitiativeService {
	void hideInitiative(@Nonnull MapView view);
	
	void showInitiative(@Nonnull MapView view, @Nonnull InitiativeLocation location);
	
	void addInitiative(@Nonnull MapView view, @Nonnull String name, int score, @Nonnull InitiativeType type);

	void reroll(@Nonnull MapView view);
	
	void removeParticipant(@Nonnull InitiativeParticipant participant);

	boolean canMoveUp(@Nonnull InitiativeParticipant participant);

	boolean canMoveDown(@Nonnull InitiativeParticipant participant);

	void moveUp(@Nonnull InitiativeParticipant participant);
	
	void moveDown(@Nonnull InitiativeParticipant participant);
	
	void select(@Nonnull InitiativeParticipant participant);
	
	void selectNext(@Nonnull MapView view);

	void setViewInitiativeMargin(@Nonnull MapView view, @Nonnull Integer margin);

	void setParticipantTotal(@Nonnull InitiativeParticipant participant, int total);
}
