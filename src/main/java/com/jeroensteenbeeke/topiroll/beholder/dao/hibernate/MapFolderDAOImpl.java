package com.jeroensteenbeeke.topiroll.beholder.dao.hibernate;

import com.jeroensteenbeeke.hyperion.solstice.data.HibernateDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.MapFolderDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapFolder;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.MapFolderFilter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "request")
class MapFolderDAOImpl extends HibernateDAO<MapFolder, MapFolderFilter> implements MapFolderDAO {

}
