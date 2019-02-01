package com.jeroensteenbeeke.topiroll.beholder.dao.hibernate;

import com.jeroensteenbeeke.hyperion.solstice.data.HibernateDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.PortraitDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.Portrait;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.PortraitFilter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

	@Component
	@Scope(value="request")  
class PortraitDAOImpl extends HibernateDAO<Portrait, PortraitFilter> implements PortraitDAO {

}
