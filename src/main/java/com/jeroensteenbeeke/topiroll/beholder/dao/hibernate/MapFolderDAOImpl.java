package com.jeroensteenbeeke.topiroll.beholder.dao.hibernate;

import com.jeroensteenbeeke.hyperion.solstice.data.HibernateDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapFolder;
import com.jeroensteenbeeke.topiroll.beholder.dao.MapFolderDAO;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;

	@Component
	@Scope(value="request")  
class MapFolderDAOImpl extends HibernateDAO<MapFolder> implements MapFolderDAO {

}
