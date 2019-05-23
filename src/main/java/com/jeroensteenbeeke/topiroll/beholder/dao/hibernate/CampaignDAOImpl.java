package com.jeroensteenbeeke.topiroll.beholder.dao.hibernate;

import com.jeroensteenbeeke.hyperion.solstice.data.HibernateDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.Campaign;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.CampaignFilter;
import com.jeroensteenbeeke.topiroll.beholder.dao.CampaignDAO;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Scope;

	@Repository
	@Scope(value="request")  
class CampaignDAOImpl extends HibernateDAO<Campaign,CampaignFilter> implements CampaignDAO {

}
