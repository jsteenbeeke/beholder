package com.jeroensteenbeeke.topiroll.beholder.entities;

import java.awt.Dimension;

import com.jeroensteenbeeke.hyperion.util.ImageUtil;
import com.jeroensteenbeeke.topiroll.beholder.util.Calculations;

public enum TokenSize {
	TINY(0.5), SMALL(1.0), MEDIUM(1.0), LARGE(2.0), HUGE(3.0), GARGANTUAN(4.0);

	private final double factor;

	private TokenSize(double factor) {
		this.factor = factor;
	}

	public byte[] resizeFor(byte[] imageData, MapView targetView, boolean isPreview) {
		ScaledMap map = targetView.getSelectedMap();
		
		double effectiveFactor = factor;
		
		if (isPreview) {
			Dimension dimensions = ImageUtil.getImageDimensions(map.getData());
			
			int targetWidth = (int) dimensions.getWidth();

			// Decrease width and height by 10% until preview
			// size has been achieved
			while (targetWidth > 640) {
				targetWidth = (int) (targetWidth * 0.9);
				effectiveFactor = effectiveFactor * 0.9;
			}
		}
		
		int size = (int) (Calculations
				.scale(map.getSquareSize())
				.toResolution(targetView.toResolution())
				.onScreenWithDiagonalSize(
						targetView.getScreenDiagonalInInches())
				* effectiveFactor);

		return ImageUtil.resize(imageData, size, size);
	}
}
