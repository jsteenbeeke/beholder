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
package com.jeroensteenbeeke.topiroll.beholder.entities;


import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

public enum PortraitVisibilityLocation {
	FULL("Full screen") {
		@Override
		public Set<PortraitVisibilityLocation> getExcludedLocations() {
			return setOf(LEFT, RIGHT, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT);
		}
	}, LEFT("Left") {
		@Override
		public Set<PortraitVisibilityLocation> getExcludedLocations() {
			return setOf(FULL, TOP_LEFT, BOTTOM_LEFT);
		}
	}, RIGHT("Right") {
		@Override
		public Set<PortraitVisibilityLocation> getExcludedLocations() {
			return setOf(FULL, TOP_RIGHT, BOTTOM_RIGHT);
		}
	}, TOP_LEFT("Top left") {
		@Override
		public Set<PortraitVisibilityLocation> getExcludedLocations() {
			return setOf(FULL, LEFT);
		}
	}, TOP_RIGHT("Top right") {
		@Override
		public Set<PortraitVisibilityLocation> getExcludedLocations() {
			return setOf(FULL, RIGHT);
		}
	}, BOTTOM_LEFT("Bottom left") {
		@Override
		public Set<PortraitVisibilityLocation> getExcludedLocations() {
			return setOf(FULL, LEFT);
		}
	}, BOTTOM_RIGHT("Bottom right") {
		@Override
		public Set<PortraitVisibilityLocation> getExcludedLocations() {
			return setOf(FULL, RIGHT);
		}
	};


	private final String displayValue;

	PortraitVisibilityLocation(String displayValue) {
		this.displayValue = displayValue;
	}

	public abstract Set<PortraitVisibilityLocation> getExcludedLocations();

	protected Set<PortraitVisibilityLocation> setOf(PortraitVisibilityLocation... locations) {
		return EnumSet.copyOf(Arrays.asList(locations));
	}

	public String getDisplayValue() {
		return displayValue;
	}

}
