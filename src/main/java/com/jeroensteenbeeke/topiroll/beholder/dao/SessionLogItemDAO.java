package com.jeroensteenbeeke.topiroll.beholder.dao;

import com.jeroensteenbeeke.hyperion.solstice.data.HibernateDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.SessionLogItem;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.SessionLogItemFilter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

@Repository
@Scope(value = "request")
public class SessionLogItemDAO
		extends HibernateDAO<SessionLogItem, SessionLogItemFilter> {

}
