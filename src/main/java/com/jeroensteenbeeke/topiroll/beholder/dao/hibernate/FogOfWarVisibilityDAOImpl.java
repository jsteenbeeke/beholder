package com.jeroensteenbeeke.topiroll.beholder.dao.hibernate;

import com.jeroensteenbeeke.hyperion.solstice.data.HibernateDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarVisibility;
import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarVisibilityDAO;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;

@Component
@Scope(value = "request")
class FogOfWarVisibilityDAOImpl extends HibernateDAO<FogOfWarVisibility>
		implements FogOfWarVisibilityDAO {

}
