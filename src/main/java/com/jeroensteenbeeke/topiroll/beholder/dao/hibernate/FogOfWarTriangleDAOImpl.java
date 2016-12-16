package com.jeroensteenbeeke.topiroll.beholder.dao.hibernate;

import com.jeroensteenbeeke.hyperion.solstice.data.HibernateDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarTriangle;
import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarTriangleDAO;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;

	@Component
	@Scope(value="request")  
class FogOfWarTriangleDAOImpl extends HibernateDAO<FogOfWarTriangle> implements FogOfWarTriangleDAO {

}
