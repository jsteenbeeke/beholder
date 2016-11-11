package com.jeroensteenbeeke.topiroll.beholder.util;

public interface Resolution {
	int getWidth();
	
	int getHeight();

	default double getRatio() {
		double w = getWidth();
		double h = getHeight();
		
		return w/h;
	}
}
