package com.jeroensteenbeeke.topiroll.beholder.dao.hibernate;

import com.jeroensteenbeeke.hyperion.solstice.data.HibernateDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarShape;
import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarShapeDAO;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;

@Component
@Scope(value = "request")
class FogOfWarShapeDAOImpl extends HibernateDAO<FogOfWarShape>
		implements FogOfWarShapeDAO {

}
