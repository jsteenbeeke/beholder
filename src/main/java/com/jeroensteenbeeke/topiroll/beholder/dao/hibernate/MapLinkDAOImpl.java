package com.jeroensteenbeeke.topiroll.beholder.dao.hibernate;

import com.jeroensteenbeeke.hyperion.solstice.data.HibernateDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.MapLinkDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapLink;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.MapLinkFilter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

@Repository
@Scope(value = "request")
class MapLinkDAOImpl extends HibernateDAO<MapLink, MapLinkFilter> implements MapLinkDAO {

}
