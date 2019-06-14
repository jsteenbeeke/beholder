package com.jeroensteenbeeke.topiroll.beholder.beans;

import com.jeroensteenbeeke.lux.ActionResult;
import com.jeroensteenbeeke.lux.TypedResult;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeParticipant;
import com.jeroensteenbeeke.topiroll.beholder.entities.SessionLogItem;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;
import io.vavr.control.Option;

import javax.annotation.Nonnull;

public interface SessionLogService {
	@Nonnull
	TypedResult<SessionLogItem> addSessionLogEntry(@Nonnull BeholderUser user, @Nonnull String entry);

	@Nonnull
	ActionResult setCompleted(@Nonnull SessionLogItem logItem);

	@Nonnull
	ActionResult setNotCompleted(@Nonnull SessionLogItem logItem);

	@Nonnull
	TypedResult<SessionLogItem> addSessionLogEntry(@Nonnull BeholderUser user, @Nonnull String description, @Nonnull Option<InitiativeParticipant> participantOrCause,
							@Nonnull TokenInstance target,
							int damage, boolean lethal);
}
