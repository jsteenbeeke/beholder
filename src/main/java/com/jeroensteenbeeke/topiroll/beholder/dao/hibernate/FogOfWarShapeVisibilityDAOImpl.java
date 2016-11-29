package com.jeroensteenbeeke.topiroll.beholder.dao.hibernate;

import com.jeroensteenbeeke.hyperion.solstice.data.HibernateDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarShapeVisibility;
import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarShapeVisibilityDAO;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;

@Component
@Scope(value = "request")
class FogOfWarShapeVisibilityDAOImpl
		extends HibernateDAO<FogOfWarShapeVisibility>
		implements FogOfWarShapeVisibilityDAO {

}
