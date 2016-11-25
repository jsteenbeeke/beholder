package com.jeroensteenbeeke.topiroll.beholder.beans;

import javax.annotation.Nonnull;

public interface URLService {
	@Nonnull
	String contextRelative(@Nonnull String relativePath);
}
