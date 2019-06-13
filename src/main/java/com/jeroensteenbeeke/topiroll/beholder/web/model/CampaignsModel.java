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
package com.jeroensteenbeeke.topiroll.beholder.web.model;

import com.google.common.collect.ImmutableList;
import com.jeroensteenbeeke.topiroll.beholder.BeholderApplication;
import com.jeroensteenbeeke.topiroll.beholder.dao.CampaignDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.entities.Campaign;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.CampaignFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.BeholderSession;
import org.apache.wicket.model.LoadableDetachableModel;

import java.util.List;

public class CampaignsModel extends LoadableDetachableModel<List<Campaign>> {
	private static final long serialVersionUID = 210740516665247926L;

	@Override
	protected List<Campaign> load() {
		BeholderUser user = BeholderSession.get().getUser();

		if (user != null) {
			CampaignFilter filter = new CampaignFilter();
			filter.dungeonMaster(user);
			filter.name().orderBy(true);

			CampaignDAO campaignDAO = BeholderApplication.get().getBean(CampaignDAO.class);

			return campaignDAO.findByFilter(filter).toJavaList();
		}

		return ImmutableList.of();
	}
}
