package com.jeroensteenbeeke.topiroll.beholder.beans;

import javax.annotation.Nonnull;

import com.jeroensteenbeeke.topiroll.beholder.entities.*;

public interface MarkerService {

	void update(@Nonnull CircleMarker marker, @Nonnull String color, int x, int y, int radius);

	void update(@Nonnull ConeMarker marker, @Nonnull String color, int x, int y, int radius, int theta);

	void update(@Nonnull CubeMarker marker, @Nonnull String color, int x, int y, int extent);

	void update(@Nonnull LineMarker marker, @Nonnull String color, int x, int y, int extent, int theta);

	void createCircle(@Nonnull MapView view, @Nonnull TokenInstance token);

	void createCone(@Nonnull MapView view, @Nonnull TokenInstance token);

	void createCube(@Nonnull MapView view, @Nonnull TokenInstance token);
	
	void createLine(@Nonnull MapView view, @Nonnull TokenInstance token);

}
