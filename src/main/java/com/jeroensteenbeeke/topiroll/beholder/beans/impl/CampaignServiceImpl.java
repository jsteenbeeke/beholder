package com.jeroensteenbeeke.topiroll.beholder.beans.impl;

import com.jeroensteenbeeke.lux.ActionResult;
import com.jeroensteenbeeke.lux.TypedResult;
import com.jeroensteenbeeke.topiroll.beholder.dao.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.entities.Campaign;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.*;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Scope;
import com.jeroensteenbeeke.topiroll.beholder.beans.CampaignService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Service
@Scope(value = "request")
class CampaignServiceImpl implements CampaignService {
	@Autowired
	private BeholderUserDAO userDAO;

	@Autowired
	private CampaignDAO campaignDAO;

	@Autowired
	private TokenDefinitionDAO tokenDefinitionDAO;

	@Autowired
	private ScaledMapDAO mapDAO;

	@Autowired
	private MapFolderDAO mapFolderDAO;

	@Autowired
	private YouTubePlaylistDAO playlistDAO;

	@Nonnull
	@Override
	public ActionResult setActiveCampaign(@Nonnull BeholderUser user, @Nullable Campaign campaign) {
		return Try.of(() -> {
			user.setActiveCampaign(campaign);
			userDAO.update(user);
			userDAO.flush();

			return ActionResult.ok();
		}).getOrElseGet(t -> ActionResult.error(t.getMessage()));
	}

	@Nonnull
	@Override
	public ActionResult deleteCampaign(@Nonnull Campaign campaign) {
		Option<String> reason = determineReasonForNotDeleting(campaign);

		return reason.map(ActionResult::error).getOrElse(ActionResult::ok).ifOk(() -> campaignDAO.delete(campaign));
	}

	private Option<String> determineReasonForNotDeleting(Campaign campaign) {
		BeholderUserFilter userFilter = new BeholderUserFilter();
		userFilter.activeCampaign(campaign);

		if (userDAO.countByFilter(userFilter) > 0) {
			return Option.of("Cannot delete campaign: still active");
		}

		TokenDefinitionFilter tokenDefinitionFilter = new TokenDefinitionFilter();
		tokenDefinitionFilter.campaign(campaign);

		if (tokenDefinitionDAO.countByFilter(tokenDefinitionFilter) > 0) {
			return Option.of("Cannot delete campaign: still has campaign-specific token definitions");
		}

		ScaledMapFilter mapFilter = new ScaledMapFilter();
		mapFilter.campaign(campaign);

		if (mapDAO.countByFilter(mapFilter) > 0) {
			return Option.of("Cannot delete campaign: still has campaign-specific maps");
		}

		MapFolderFilter folderFilter = new MapFolderFilter();
		folderFilter.campaign(campaign);

		if (mapFolderDAO.countByFilter(folderFilter) > 0) {
			return Option.of("Cannot delete campaign: still has campaign-specific map folders");
		}

		YouTubePlaylistFilter youTubePlaylistFilter = new YouTubePlaylistFilter();
		youTubePlaylistFilter.campaign(campaign);

		if (playlistDAO.countByFilter(youTubePlaylistFilter) > 0) {
			return Option.of("Cannot delete campaign: still has campaign-specific playlists");
		}


		return Option.none();
	}

	@Override
	public boolean isDeleteAllowed(@Nonnull Campaign campaign) {
		return determineReasonForNotDeleting(campaign).isEmpty();
	}
}
