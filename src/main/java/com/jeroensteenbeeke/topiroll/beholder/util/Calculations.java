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
package com.jeroensteenbeeke.topiroll.beholder.util;

import org.jetbrains.annotations.NotNull;
import java.awt.Dimension;

public final class Calculations {
	public static final class ScaleCalculationFinalizer {
		private final int pixels;

		private final Dimension resolution;

		private ScaleCalculationFinalizer(int pixels, Dimension resolution) {
			this.pixels = pixels;
			this.resolution = resolution;
		}

		public double onScreenWithDiagonalSize(double screenDiagonalInInches) {
			double oneInchSquareInPixels = oneInchSquareInPixels(resolution,
					screenDiagonalInInches);

			return oneInchSquareInPixels / pixels;
		}
	}

	public static final class ScaleCalculation {

		private final int pixels;

		private ScaleCalculation(int pixels) {
			this.pixels = pixels;
		}

		public ScaleCalculationFinalizer toResolution(Dimension resolution) {
			return new ScaleCalculationFinalizer(pixels, resolution);
		}

	}

	private Calculations() {

	}

	/**
	 * Calculate the size in pixels of a 1 inch square (representing five feet
	 * in-game) given a physical screen's diagonal length
	 * and screen resolution.
	 * 
	 * @param resolution
	 *            The resolution the screen is currently set to
	 * @param screenDiagonalInInches
	 *            The screen's diagonal length in inches
	 * @return The number of pixels (length and width) of a 1 inch
	 */
	public static long oneInchSquareInPixels(Dimension resolution,
			double screenDiagonalInInches) {
		final double horizontalInches = horizontalInches(screenDiagonalInInches,
				resolution);

		final double verticalInches = verticalInches(screenDiagonalInInches,
				resolution);

		final double horizontalInchesPerPixel = horizontalInches
				/ resolution.getWidth();
		final double verticalInchesPerPixel = verticalInches
				/ resolution.getHeight();

		final double deviation = horizontalInchesPerPixel
				- verticalInchesPerPixel;

		if (deviation > 0.01) {
			throw new IllegalStateException(
					"Incorrect math, deviation between vertical and horizontal measure");
		}

		// Take the average of the two
		final double inchesPerPixel = (horizontalInchesPerPixel
				+ verticalInchesPerPixel) / 2;

		return Math.round(1.0 / inchesPerPixel);
	}

	public static double screenDiagonalTheta(Dimension resolution) {
		return Math.atan2(resolution.getHeight(), resolution.getWidth());
	}

	public static double verticalInches(final double screenDiagonalInInches,
			final Dimension resolution) {
		// SOH CAH TOA
		// sin theta = Opposite / Hypothenuse -> Opposite = hypothenuse * sin
		// theta
		return screenDiagonalInInches
				* Math.sin(screenDiagonalTheta(resolution));
	}

	public static double horizontalInches(final double screenDiagonalInInches,
			final Dimension resolution) {
		// SOH CAH TOA
		// cos theta = Adjacent / Hypothenuse -> Adjacent = hypothenuse * cos
		// theta
		return screenDiagonalInInches
				* Math.cos(screenDiagonalTheta(resolution));
	}

	/**
	 * Start a calculation to see what factor a square of a given number of
	 * pixels needs to be modified to fit the
	 * screen.
	 * 
	 * @param pixels
	 *            The number of pixels a square on the given map image occupies
	 * @return A ScaleCalculation object, a builder to
	 */
	@NotNull
	public static ScaleCalculation scale(int pixels) {
		return new ScaleCalculation(pixels);
	}

	public static int getTheta(int sx, int sy, int tx, int ty) {
		int theta = (int) Math.toDegrees(Math.atan2(ty - sy, tx - sx));

		while (theta < 0) {
			theta = theta + 360;
		}

		while (theta > 360) {
			theta = theta - 360;
		}

		return theta;
	}

	public static int distance(int sx, int sy, int tx, int ty) {
		return (int) Math.sqrt(Math.pow(tx-sx, 2) + Math.pow(ty-sy, 2));
	}
}
