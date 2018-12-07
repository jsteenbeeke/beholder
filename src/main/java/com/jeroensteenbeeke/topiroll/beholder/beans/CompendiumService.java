package com.jeroensteenbeeke.topiroll.beholder.beans;

import com.jeroensteenbeeke.lux.TypedResult;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.entities.CompendiumEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface CompendiumService {
	List<CompendiumEntry> performSearch(BeholderUser user, String query);

	boolean articleExists(String path);

	TypedResult<CompendiumEntry> createImportedArticle(String title, String path, String html);

	void pinEntry(@Nullable BeholderUser user, @Nonnull CompendiumEntry entry);

	void unpinEntry(@Nullable BeholderUser user, @Nonnull CompendiumEntry entry);
}
