/**
 * This file is part of Beholder
 * (C) 2016-2019 Jeroen Steenbeeke
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
import com.jeroensteenbeeke.topiroll.beholder.annotation.NoTransactionRequired;
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
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Service
@Scope(value = "request")
public class CampaignService {
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
	@Transactional
	public ActionResult setActiveCampaign(@Nonnull BeholderUser user, @Nullable Campaign campaign) {
		return Try.of(() -> {
			user.setActiveCampaign(campaign);
			userDAO.update(user);
			userDAO.flush();

			return ActionResult.ok();
		}).getOrElseGet(t -> ActionResult.error(t.getMessage()));
	}

	@Nonnull
	@Transactional
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

	@NoTransactionRequired
	public boolean isDeleteAllowed(@Nonnull Campaign campaign) {
		return determineReasonForNotDeleting(campaign).isEmpty();
	}

	@Transactional
	public void deactivateCurrentCampaign(@Nonnull BeholderUser user) {
		user.setActiveCampaign(null);
		userDAO.update(user);
		userDAO.flush();
	}
}
