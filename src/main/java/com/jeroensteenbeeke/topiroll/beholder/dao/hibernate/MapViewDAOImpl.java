package com.jeroensteenbeeke.topiroll.beholder.dao.hibernate;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jeroensteenbeeke.hyperion.solstice.data.HibernateDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.MapViewDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;

	@Component
	@Scope(value="request")  
class MapViewDAOImpl extends HibernateDAO<MapView> implements MapViewDAO {

}
