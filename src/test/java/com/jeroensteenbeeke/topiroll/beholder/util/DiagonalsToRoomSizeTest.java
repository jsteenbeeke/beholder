package com.jeroensteenbeeke.topiroll.beholder.util;

import org.junit.Test;

public class DiagonalsToRoomSizeTest {
	@Test
	public void diagonals() {
		double[] diagonals = {
				32.0, 40.0, 43.0, 48.0
				
		};
		
		for (double diag: diagonals) {
			double h = Calculations.horizontalInches(diag, Resolutions.hd720);
			double v = Calculations.verticalInches(diag, Resolutions.hd720);
			
			System.out.printf("%d\" diagonaal: %d x %d squares", Math.round(diag), Math.round(h), Math.round(v));
			System.out.println();
		}
	}
}
