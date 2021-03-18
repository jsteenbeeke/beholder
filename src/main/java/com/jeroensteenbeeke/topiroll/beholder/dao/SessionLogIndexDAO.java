package com.jeroensteenbeeke.topiroll.beholder.dao;

import com.jeroensteenbeeke.hyperion.solstice.data.HibernateDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.SessionLogIndex;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.SessionLogIndexFilter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

@Repository
@Scope(value = "request")
public class SessionLogIndexDAO
		extends HibernateDAO<SessionLogIndex, SessionLogIndexFilter> {

}
