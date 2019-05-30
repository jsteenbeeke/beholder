package com.jeroensteenbeeke.topiroll.beholder.beans;

import com.jeroensteenbeeke.lux.ActionResult;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.entities.Campaign;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface CampaignService {
	@Nonnull
	ActionResult setActiveCampaign(@Nonnull BeholderUser user, @Nullable Campaign campaign);

	@Nonnull
	ActionResult deleteCampaign(@Nonnull Campaign campaign);

	boolean isDeleteAllowed(@Nonnull Campaign campaign);

	void deactivateCurrentCampaign(@Nonnull BeholderUser user);
}
