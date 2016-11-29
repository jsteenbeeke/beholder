package com.jeroensteenbeeke.topiroll.beholder.dao.hibernate;

import com.jeroensteenbeeke.hyperion.solstice.data.HibernateDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.Effect;
import com.jeroensteenbeeke.topiroll.beholder.dao.EffectDAO;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;

	@Component
	@Scope(value="request")  
class EffectDAOImpl extends HibernateDAO<Effect> implements EffectDAO {

}
