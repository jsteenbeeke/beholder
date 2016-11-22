package com.jeroensteenbeeke.topiroll.beholder.beans;

import javax.annotation.Nonnull;

import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;

public interface IAccountInitializer {
	void onAccountCreated(@Nonnull BeholderUser user);
}
