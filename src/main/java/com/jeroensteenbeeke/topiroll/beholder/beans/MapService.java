package com.jeroensteenbeeke.topiroll.beholder.beans;

import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;

public interface MapService {
	ScaledMap createMap(String name, int squareSize, byte[] data);
}
