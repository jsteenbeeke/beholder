package com.jeroensteenbeeke.topiroll.beholder.dao.hibernate;

import com.jeroensteenbeeke.hyperion.solstice.data.HibernateDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.PortraitVisibilityDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.PortraitVisibility;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.PortraitVisibilityFilter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

	@Component
	@Scope(value="request")  
class PortraitVisibilityDAOImpl extends HibernateDAO<PortraitVisibility, PortraitVisibilityFilter> implements PortraitVisibilityDAO {

}
