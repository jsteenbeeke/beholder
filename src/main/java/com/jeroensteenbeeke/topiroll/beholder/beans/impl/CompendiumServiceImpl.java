package com.jeroensteenbeeke.topiroll.beholder.beans.impl;

import com.jeroensteenbeeke.hyperion.util.TypedActionResult;
import com.jeroensteenbeeke.topiroll.beholder.beans.CompendiumService;
import com.jeroensteenbeeke.topiroll.beholder.dao.CompendiumEntryDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.CompendiumEntry;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.CompendiumEntryFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope(value="request")
class CompendiumServiceImpl implements CompendiumService {
		private final CompendiumEntryDAO dao;

		@Autowired
		public CompendiumServiceImpl(CompendiumEntryDAO dao) {
			this.dao = dao;
		}

		@Override
		public boolean articleExists(String path) {
			CompendiumEntryFilter filter = new CompendiumEntryFilter();
			filter.originalPath(path);

			return dao.countByFilter(filter) > 0;
		}

		@Override
		public TypedActionResult<CompendiumEntry> createArticle(String title, String path, String html) {
			if (!articleExists(path)) {
				CompendiumEntry entry = new CompendiumEntry();
				entry.setBody(html);
				entry.setOriginalPath(path);
				entry.setTitle(title);

				dao.save(entry);

				return TypedActionResult.ok(entry);
			}

			return TypedActionResult.fail("Path already exists");
		}

	@Override
	public List<CompendiumEntry> performSearch(String query) {
		CompendiumEntryFilter filter = new CompendiumEntryFilter();
		filter.title().ilike(String.format("%%%s%%", query));
		filter.title().orderBy(true);

		return dao.findByFilter(filter);
	}
}
