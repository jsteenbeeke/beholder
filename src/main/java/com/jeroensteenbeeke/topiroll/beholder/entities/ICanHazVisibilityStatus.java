package com.jeroensteenbeeke.topiroll.beholder.entities;

import javax.annotation.Nonnull;

import com.jeroensteenbeeke.topiroll.beholder.web.resources.AbstractFogOfWarPreviewResource;

public interface ICanHazVisibilityStatus {
	@Nonnull
	VisibilityStatus getStatus(@Nonnull MapView view);

	@Nonnull
	String getDescription();

	@Nonnull
	AbstractFogOfWarPreviewResource createThumbnailResource(int size);
}
