package com.jeroensteenbeeke.topiroll.beholder.dao.hibernate;

import com.jeroensteenbeeke.hyperion.solstice.data.HibernateDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.CircleMarker;
import com.jeroensteenbeeke.topiroll.beholder.dao.CircleMarkerDAO;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;

	@Component
	@Scope(value="request")  
class CircleMarkerDAOImpl extends HibernateDAO<CircleMarker> implements CircleMarkerDAO {

}
