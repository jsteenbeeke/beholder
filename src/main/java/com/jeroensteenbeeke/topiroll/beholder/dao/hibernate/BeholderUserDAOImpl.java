package com.jeroensteenbeeke.topiroll.beholder.dao.hibernate;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jeroensteenbeeke.hyperion.solstice.data.HibernateDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.BeholderUserDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;

@Component
@Scope(value = "request")
class BeholderUserDAOImpl extends HibernateDAO<BeholderUser>
		implements BeholderUserDAO {

}
