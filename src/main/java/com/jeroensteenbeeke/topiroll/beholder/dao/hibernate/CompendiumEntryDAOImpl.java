package com.jeroensteenbeeke.topiroll.beholder.dao.hibernate;

import com.jeroensteenbeeke.hyperion.solstice.data.HibernateDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.CompendiumEntryDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.CompendiumEntry;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.CompendiumEntryFilter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

	@Component
	@Scope(value="request")  
class CompendiumEntryDAOImpl extends HibernateDAO<CompendiumEntry, CompendiumEntryFilter> implements CompendiumEntryDAO {

}
