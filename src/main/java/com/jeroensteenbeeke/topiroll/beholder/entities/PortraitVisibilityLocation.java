package com.jeroensteenbeeke.topiroll.beholder.entities;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import javax.sound.sampled.Port;
import java.util.List;
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

	protected Set<PortraitVisibilityLocation> setOf(PortraitVisibilityLocation...locations) {
		return Sets.newEnumSet(Lists.newArrayList(locations), PortraitVisibilityLocation.class);
	}

	public String getDisplayValue() {
		return displayValue;
	}

}
