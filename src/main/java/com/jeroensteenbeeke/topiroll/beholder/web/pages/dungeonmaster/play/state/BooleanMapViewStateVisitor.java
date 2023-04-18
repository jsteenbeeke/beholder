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
