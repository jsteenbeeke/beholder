package com.jeroensteenbeeke.topiroll.beholder.beans.impl;

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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Scope(value = "request")
class SessionLogServiceImpl implements SessionLogService {
	@Autowired
	private SessionLogIndexDAO sessionLogIndexDAO;

	@Autowired
	private SessionLogItemDAO sessionLogItemDAO;

	@Nonnull
	@Override
	@Transactional
	public TypedResult<SessionLogItem> addSessionLogEntry(@Nonnull BeholderUser user, @Nonnull String entry) {
		return getOrCreateTodayIndex(user).map(index -> createSessionLogItem(user, entry, index));
	}

	private SessionLogItem createSessionLogItem(@Nonnull BeholderUser user, @Nonnull String entry, SessionLogIndex index) {
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

	@Override
	public TypedResult<SessionLogItem> addSessionLogEntry(@Nonnull BeholderUser user, @Nonnull String description, @Nonnull Option<InitiativeParticipant> participantOrCause, @Nonnull TokenInstance target, int damage, boolean lethal) {
		return getOrCreateTodayIndex(user).map(index -> {
			final var killDescription = lethal ? ", killing " + target.getLabel() : "";

			String entry = participantOrCause.map(InitiativeParticipant::getName)
											  .map(name -> String.format("%s attacks %s using %s, dealing %d damage%s", name, target
												  .getLabel(), description, damage, killDescription))
											  .getOrElse(() -> String.format("%s receives %d damage from %s%s", target.getLabel(), damage, description, killDescription));


			return createSessionLogItem(user, entry, index);
		});
	}

	@Nonnull
	@Override
	public ActionResult setCompleted(@Nonnull SessionLogItem logItem) {
		logItem.setCompleted(true);
		sessionLogItemDAO.update(logItem);
		sessionLogItemDAO.flush();

		return ActionResult.ok();
	}

	@Nonnull
	@Override
	public ActionResult setNotCompleted(@Nonnull SessionLogItem logItem) {
		logItem.setCompleted(false);
		sessionLogItemDAO.update(logItem);
		sessionLogItemDAO.flush();

		return ActionResult.ok();
	}

	private TypedResult<SessionLogIndex> getOrCreateTodayIndex(@Nonnull BeholderUser user) {
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
