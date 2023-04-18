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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.awt.*;

public class LocationClickedState implements IMapViewState {

	private static final long serialVersionUID = 3637551757897107725L;

	private final Point clickedLocation;

	private final Point previousClickedLocation;

	LocationClickedState(@NotNull Point clickedLocation, @Nullable Point previousClickedLocation) {
		this.clickedLocation = clickedLocation;
		this.previousClickedLocation = previousClickedLocation;
	}

	@Override
	public IMapViewState onLocationClicked(Point clickedLocation) {
		return new LocationClickedState(clickedLocation, this.clickedLocation);
	}

	public Point getClickedLocation() {
		return clickedLocation;
	}

	public Point getPreviousClickedLocation() {
		return previousClickedLocation;
	}

	@Override
	public <T> T visit(IMapViewStateVisitor<T> visitor) {
		return visitor.visit(this);
	}

	@Override
	public void detach() {

	}
}
