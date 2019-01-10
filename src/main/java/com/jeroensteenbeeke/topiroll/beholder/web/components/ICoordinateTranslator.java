package com.jeroensteenbeeke.topiroll.beholder.web.components;

import java.io.Serializable;

public interface ICoordinateTranslator extends Serializable {
	int translateToRealImageSize(int number);

	int translateToScaledImageSize(int number);
}
