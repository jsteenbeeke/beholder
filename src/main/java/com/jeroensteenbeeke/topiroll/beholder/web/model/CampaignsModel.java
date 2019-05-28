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
