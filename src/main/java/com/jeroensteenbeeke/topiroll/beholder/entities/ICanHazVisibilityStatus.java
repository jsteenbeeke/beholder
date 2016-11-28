package com.jeroensteenbeeke.topiroll.beholder.entities;

import javax.annotation.Nonnull;

import com.jeroensteenbeeke.topiroll.beholder.web.resources.AbstractFogOfWarPreviewResource;

public interface ICanHazVisibilityStatus {
	@Nonnull
	VisibilityStatus getStatus();
	
	void setStatus(@Nonnull VisibilityStatus status);
	
	@Nonnull
	String getDescription();
	
	@Nonnull
	AbstractFogOfWarPreviewResource createThumbnailResource(int size);
}
