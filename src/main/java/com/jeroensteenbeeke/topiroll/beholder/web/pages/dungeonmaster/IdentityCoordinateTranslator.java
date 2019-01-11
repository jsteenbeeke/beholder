package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster;

import com.jeroensteenbeeke.topiroll.beholder.web.components.ICoordinateTranslator;

public class IdentityCoordinateTranslator implements ICoordinateTranslator {
	private static final long serialVersionUID = -5907393995407695874L;

	@Override
	public int translateToRealImageSize(int number) {
		return number;
	}

	@Override
	public int translateToScaledImageSize(int number) {
		return number;
	}
}
