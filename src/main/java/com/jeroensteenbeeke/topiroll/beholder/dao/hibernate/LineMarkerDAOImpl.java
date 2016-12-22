package com.jeroensteenbeeke.topiroll.beholder.dao.hibernate;

import com.jeroensteenbeeke.hyperion.solstice.data.HibernateDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.LineMarker;
import com.jeroensteenbeeke.topiroll.beholder.dao.LineMarkerDAO;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;

	@Component
	@Scope(value="request")  
class LineMarkerDAOImpl extends HibernateDAO<LineMarker> implements LineMarkerDAO {

}
