package com.jeroensteenbeeke.topiroll.beholder.dao.hibernate;

import com.jeroensteenbeeke.hyperion.solstice.data.HibernateDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarGroup;
import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarGroupDAO;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;

@Component
@Scope(value = "request")
class FogOfWarGroupDAOImpl extends HibernateDAO<FogOfWarGroup>
		implements FogOfWarGroupDAO {

}
