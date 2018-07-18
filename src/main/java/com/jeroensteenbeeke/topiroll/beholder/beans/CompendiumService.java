package com.jeroensteenbeeke.topiroll.beholder.beans;

import com.jeroensteenbeeke.hyperion.util.TypedActionResult;
import com.jeroensteenbeeke.topiroll.beholder.entities.CompendiumEntry;

import java.util.List;

public interface CompendiumService {
	List<CompendiumEntry> performSearch(String query);

	boolean articleExists(String path);

	TypedActionResult<CompendiumEntry> createArticle(String title, String path, String html);
}
