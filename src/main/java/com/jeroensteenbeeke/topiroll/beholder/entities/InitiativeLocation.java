package com.jeroensteenbeeke.topiroll.beholder.entities;

public enum InitiativeLocation {
	TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT;
	
	public String toJS() {
		return name().toLowerCase().replace('_', '-');
	}

	public String getPrettyName() {
		
		final String base = name().replace('_', ' ');
		
		return base.substring(0, 1).concat(base.substring(1).toLowerCase());
	}
}
