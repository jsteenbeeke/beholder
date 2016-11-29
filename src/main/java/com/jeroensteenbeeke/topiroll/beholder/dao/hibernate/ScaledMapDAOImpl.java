package com.jeroensteenbeeke.topiroll.beholder.dao.hibernate;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jeroensteenbeeke.hyperion.solstice.data.HibernateDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.ScaledMapDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;

@Component
@Scope(value = "request")
class ScaledMapDAOImpl extends HibernateDAO<ScaledMap> implements ScaledMapDAO {

}
