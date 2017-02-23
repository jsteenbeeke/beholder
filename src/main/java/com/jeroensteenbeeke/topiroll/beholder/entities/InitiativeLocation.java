package com.jeroensteenbeeke.topiroll.beholder.entities;

public enum InitiativeLocation {
	TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT;
	
	public String toJS() {
		return name().toLowerCase().replace('_', '-');
	}
}
