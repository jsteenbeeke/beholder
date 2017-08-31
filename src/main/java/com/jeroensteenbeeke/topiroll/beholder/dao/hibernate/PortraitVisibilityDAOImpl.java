package com.jeroensteenbeeke.topiroll.beholder.dao.hibernate;

import com.jeroensteenbeeke.hyperion.solstice.data.HibernateDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.PortraitVisibility;
import com.jeroensteenbeeke.topiroll.beholder.dao.PortraitVisibilityDAO;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;

	@Component
	@Scope(value="request")  
class PortraitVisibilityDAOImpl extends HibernateDAO<PortraitVisibility> implements PortraitVisibilityDAO {

}
