package com.jeroensteenbeeke.topiroll.beholder.beans;

import javax.annotation.Nonnull;

import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.util.JSBuilder;

public interface IClipPathContributor {
	/**
	 * Indicates when this renderer should execute relative to other renderes.
	 * Executed in ascending order
	 * 
	 * @return The priority of this renderer
	 */
	int getPriority();

	void contribute(@Nonnull JSBuilder builder, @Nonnull String contextVariable,
			@Nonnull MapView mapView, boolean previewMode);
}
