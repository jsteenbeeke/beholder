package com.jeroensteenbeeke.topiroll.beholder.beans;

import javax.annotation.Nonnull;

import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;

public interface MapService {
	@Nonnull
	ScaledMap createMap(@Nonnull BeholderUser user, @Nonnull String name, int squareSize, byte[] data);
	
	void selectMap(@Nonnull MapView view, @Nonnull ScaledMap map);
	
	void unselectMap(@Nonnull MapView view);

	void addFogOfWarCircle(ScaledMap map, int radius,
			int offsetX, int offsetY);
	
	void addFogOfWarRect(ScaledMap map, int width, int height,
			int offsetX, int offsetY);
}
