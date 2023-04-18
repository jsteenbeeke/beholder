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

import com.jeroensteenbeeke.lux.ActionResult;
import com.jeroensteenbeeke.lux.TypedResult;
import com.jeroensteenbeeke.topiroll.beholder.beans.SessionLogService;
import com.jeroensteenbeeke.topiroll.beholder.dao.SessionLogIndexDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.SessionLogItemDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.SessionLogIndexFilter;
import io.vavr.control.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Scope(value = "request")
public class SessionLogService {
	@Autowired
	private SessionLogIndexDAO sessionLogIndexDAO;

	@Autowired
	private SessionLogItemDAO sessionLogItemDAO;

	@NotNull
	@Transactional
	public TypedResult<SessionLogItem> addSessionLogEntry(@NotNull BeholderUser user, @NotNull String entry) {
		return getOrCreateTodayIndex(user).map(index -> createSessionLogItem(user, entry, index));
	}

	private SessionLogItem createSessionLogItem(@NotNull BeholderUser user, @NotNull String entry, SessionLogIndex index) {
		SessionLogItem item = new SessionLogItem();
		item.setEventTime(LocalDateTime.now());
		item.setEventDescription(entry);
		item.setLogIndex(index);
		item.setUser(user);
		item.setCompleted(false);

		sessionLogItemDAO.save(item);
		sessionLogItemDAO.flush();

		return item;
	}

	@NotNull
	@Transactional
	public TypedResult<SessionLogItem> addSessionLogEntry(@NotNull BeholderUser user, @NotNull String description, @NotNull Option<InitiativeParticipant> participantOrCause, @NotNull TokenInstance target, int damage, boolean lethal) {
		return getOrCreateTodayIndex(user).map(index -> {
			final var killDescription = lethal ? ", killing " + target.getLabel() : "";

			String entry = participantOrCause.map(InitiativeParticipant::getName)
											  .map(name -> String.format("%s attacks %s using %s, dealing %d damage%s", name, target
												  .getLabel(), description, damage, killDescription))
											  .getOrElse(() -> String.format("%s receives %d damage from %s%s", target.getLabel(), damage, description, killDescription));


			return createSessionLogItem(user, entry, index);
		});
	}

	@NotNull
	@Transactional
	public ActionResult setCompleted(@NotNull SessionLogItem logItem) {
		logItem.setCompleted(true);
		sessionLogItemDAO.update(logItem);
		sessionLogItemDAO.flush();

		return ActionResult.ok();
	}

	@NotNull
	@Transactional
	public ActionResult setNotCompleted(@NotNull SessionLogItem logItem) {
		logItem.setCompleted(false);
		sessionLogItemDAO.update(logItem);
		sessionLogItemDAO.flush();

		return ActionResult.ok();
	}

	private TypedResult<SessionLogIndex> getOrCreateTodayIndex(@NotNull BeholderUser user) {
		LocalDate today = LocalDate.now();

		SessionLogIndexFilter indexFilter = new SessionLogIndexFilter();
		indexFilter.owner(user);
		indexFilter.day(today);

		SessionLogIndex index;

		if (sessionLogIndexDAO.countByFilter(indexFilter) == 0) {
			index = new SessionLogIndex();
			index.setDay(today);
			index.setOwner(user);
			sessionLogIndexDAO.save(index);

			return TypedResult.ok(index);
		} else {
			return sessionLogIndexDAO
				.getUniqueByFilter(indexFilter)
				.map(TypedResult::ok)
				.getOrElse(() -> TypedResult.fail("Could not uniquely determine session log index"));
		}
	}
}
