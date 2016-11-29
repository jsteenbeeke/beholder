package com.jeroensteenbeeke.topiroll.beholder.dao.hibernate;

import com.jeroensteenbeeke.hyperion.solstice.data.HibernateDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarGroupVisibility;
import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarGroupVisibilityDAO;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;

@Component
@Scope(value = "request")
class FogOfWarGroupVisibilityDAOImpl
		extends HibernateDAO<FogOfWarGroupVisibility>
		implements FogOfWarGroupVisibilityDAO {

}
