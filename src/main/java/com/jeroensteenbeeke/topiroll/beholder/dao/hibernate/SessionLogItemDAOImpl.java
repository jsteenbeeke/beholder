package com.jeroensteenbeeke.topiroll.beholder.dao.hibernate;

import com.jeroensteenbeeke.hyperion.solstice.data.HibernateDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.SessionLogItem;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.SessionLogItemFilter;
import com.jeroensteenbeeke.topiroll.beholder.dao.SessionLogItemDAO;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Scope;

	@Repository
	@Scope(value="request")  
class SessionLogItemDAOImpl extends HibernateDAO<SessionLogItem,SessionLogItemFilter> implements SessionLogItemDAO {

}
