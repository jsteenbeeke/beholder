package com.jeroensteenbeeke.topiroll.beholder.dao.hibernate;

import com.jeroensteenbeeke.hyperion.solstice.data.HibernateDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.SessionLogIndex;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.SessionLogIndexFilter;
import com.jeroensteenbeeke.topiroll.beholder.dao.SessionLogIndexDAO;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Scope;

	@Repository
	@Scope(value="request")  
class SessionLogIndexDAOImpl extends HibernateDAO<SessionLogIndex,SessionLogIndexFilter> implements SessionLogIndexDAO {

}
