package com.jeroensteenbeeke.topiroll.beholder.dao.hibernate;

import com.jeroensteenbeeke.hyperion.solstice.data.HibernateDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapLink;
import com.jeroensteenbeeke.topiroll.beholder.dao.MapLinkDAO;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Scope;

	@Repository
	@Scope(value="request")  
class MapLinkDAOImpl extends HibernateDAO<MapLink> implements MapLinkDAO {

}
