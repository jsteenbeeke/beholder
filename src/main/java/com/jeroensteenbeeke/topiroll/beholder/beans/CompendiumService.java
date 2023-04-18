/*
 * This file is part of Beholder
 * Copyright (C) 2016 - 2023 Jeroen Steenbeeke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jeroensteenbeeke.topiroll.beholder.beans;

import com.jeroensteenbeeke.lux.TypedResult;
import com.jeroensteenbeeke.topiroll.beholder.dao.CompendiumEntryDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.PinnedCompendiumEntryDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.entities.CompendiumEntry;
import com.jeroensteenbeeke.topiroll.beholder.entities.PinnedCompendiumEntry;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.CompendiumEntryFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;

@Service
@Scope(value = "request")
public class CompendiumService {
	private final CompendiumEntryDAO dao;

	private final PinnedCompendiumEntryDAO pinningDAO;

	@Autowired
	public CompendiumService(CompendiumEntryDAO dao, PinnedCompendiumEntryDAO pinningDAO) {
		this.dao = dao;
		this.pinningDAO = pinningDAO;
	}

	@Transactional
	public boolean articleExists(String path) {
		CompendiumEntryFilter filter = new CompendiumEntryFilter();
		filter.originalPath(path);

		return dao.countByFilter(filter) > 0;
	}

	@Transactional
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

	@Transactional
	public List<CompendiumEntry> performSearch(BeholderUser user, String query) {
		CompendiumEntryFilter filter = new CompendiumEntryFilter();
		filter.title().ilike(String.format("%%%s%%", query));
		filter.author().equalToOrNull(user);
		filter.title().orderBy(true);
		user.activeCampaign().peek(c -> filter.campaign().isNull().orCampaign(c));

		return dao.findByFilter(filter).toJavaList();
	}

	@Transactional
	public void pinEntry(@Nullable BeholderUser user, @NotNull CompendiumEntry entry) {
		if (user != null && entry.getPinnedBy().stream().map(PinnedCompendiumEntry::getPinnedBy).noneMatch(user::equals)) {
			pinningDAO.save(new PinnedCompendiumEntry(user, entry));
		}
	}

	@Transactional
	public void unpinEntry(@Nullable BeholderUser user, @NotNull CompendiumEntry entry) {
		if (user != null) {
			entry.getPinnedBy().stream().filter(pe -> user.equals(pe.getPinnedBy())).forEach(pinningDAO::delete);
		}
	}
}
