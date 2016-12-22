package com.jeroensteenbeeke.topiroll.beholder.dao.hibernate;

import com.jeroensteenbeeke.hyperion.solstice.data.HibernateDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.CubeMarker;
import com.jeroensteenbeeke.topiroll.beholder.dao.CubeMarkerDAO;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;

	@Component
	@Scope(value="request")  
class CubeMarkerDAOImpl extends HibernateDAO<CubeMarker> implements CubeMarkerDAO {

}
