package com.jeroensteenbeeke.topiroll.beholder.dao.hibernate;

import com.jeroensteenbeeke.hyperion.solstice.data.HibernateDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.AreaMarker;
import com.jeroensteenbeeke.topiroll.beholder.dao.AreaMarkerDAO;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;

	@Component
	@Scope(value="request")  
class AreaMarkerDAOImpl extends HibernateDAO<AreaMarker> implements AreaMarkerDAO {

}
