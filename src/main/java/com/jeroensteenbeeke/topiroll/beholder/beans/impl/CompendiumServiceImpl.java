package com.jeroensteenbeeke.topiroll.beholder.beans.impl;

import com.jeroensteenbeeke.lux.TypedResult;
import com.jeroensteenbeeke.topiroll.beholder.beans.CompendiumService;
import com.jeroensteenbeeke.topiroll.beholder.dao.CompendiumEntryDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.PinnedCompendiumEntryDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.entities.CompendiumEntry;
import com.jeroensteenbeeke.topiroll.beholder.entities.PinnedCompendiumEntry;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.CompendiumEntryFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@Component
@Scope(value = "request")
class CompendiumServiceImpl implements CompendiumService {
	private final CompendiumEntryDAO dao;

	private final PinnedCompendiumEntryDAO pinningDAO;

	@Autowired
	public CompendiumServiceImpl(CompendiumEntryDAO dao, PinnedCompendiumEntryDAO pinningDAO) {
		this.dao = dao;
		this.pinningDAO = pinningDAO;
	}

	@Override
	public boolean articleExists(String path) {
		CompendiumEntryFilter filter = new CompendiumEntryFilter();
		filter.originalPath(path);

		return dao.countByFilter(filter) > 0;
	}

	@Override
	public TypedResult<CompendiumEntry> createImportedArticle(String title, String path, String html) {
		if (!articleExists(path)) {
			CompendiumEntry entry = new CompendiumEntry();
			entry.setBody(html);
			entry.setOriginalPath(path);
			entry.setTitle(title);

			dao.save(entry);

			return TypedResult.ok(entry);
		}

		return TypedResult.fail("Path already exists");
	}

	@Override
	public List<CompendiumEntry> performSearch(BeholderUser user, String query) {
		CompendiumEntryFilter filter = new CompendiumEntryFilter();
		filter.title().ilike(String.format("%%%s%%", query));
		filter.author().equalToOrNull(user);
		filter.title().orderBy(true);

		return dao.findByFilter(filter).toJavaList();
	}

	@Override
	public void pinEntry(@Nullable BeholderUser user, @Nonnull CompendiumEntry entry) {
		if (user != null && entry.getPinnedBy().stream().map(PinnedCompendiumEntry::getPinnedBy).noneMatch(user::equals)) {
			pinningDAO.save(new PinnedCompendiumEntry(user, entry));
		}
	}

	@Override
	public void unpinEntry(@Nullable BeholderUser user, @Nonnull CompendiumEntry entry) {
		if (user != null) {
			entry.getPinnedBy().stream().filter(pe -> user.equals(pe.getPinnedBy())).forEach(pinningDAO::delete);
		}
	}
}
