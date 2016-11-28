package com.jeroensteenbeeke.topiroll.beholder.beans;

import java.util.List;

import javax.annotation.Nonnull;

import com.jeroensteenbeeke.hyperion.util.TypedActionResult;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarGroup;
import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarShape;
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

	TypedActionResult<FogOfWarGroup> createGroup(@Nonnull ScaledMap map, @Nonnull String name,
			@Nonnull List<FogOfWarShape> shapes);
}
