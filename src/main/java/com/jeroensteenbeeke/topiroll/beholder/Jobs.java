package com.jeroensteenbeeke.topiroll.beholder;

import com.jeroensteenbeeke.hyperion.tardis.scheduler.TaskGroup;

public enum Jobs implements TaskGroup {
	;

	private final String descriptor;
	
	private Jobs(String descriptor) {
		this.descriptor = descriptor;
	}
	
	@Override
	public String getDescriptor() {
		return descriptor;
	}
}