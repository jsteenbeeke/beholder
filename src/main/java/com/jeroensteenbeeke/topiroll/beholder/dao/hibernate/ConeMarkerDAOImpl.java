package com.jeroensteenbeeke.topiroll.beholder.dao.hibernate;

import com.jeroensteenbeeke.hyperion.solstice.data.HibernateDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.ConeMarker;
import com.jeroensteenbeeke.topiroll.beholder.dao.ConeMarkerDAO;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;

	@Component
	@Scope(value="request")  
class ConeMarkerDAOImpl extends HibernateDAO<ConeMarker> implements ConeMarkerDAO {

}
