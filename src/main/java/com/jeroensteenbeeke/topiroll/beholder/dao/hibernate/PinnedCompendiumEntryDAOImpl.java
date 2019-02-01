package com.jeroensteenbeeke.topiroll.beholder.dao.hibernate;

import com.jeroensteenbeeke.hyperion.solstice.data.HibernateDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.PinnedCompendiumEntryDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.PinnedCompendiumEntry;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.PinnedCompendiumEntryFilter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "request")
class PinnedCompendiumEntryDAOImpl extends HibernateDAO<PinnedCompendiumEntry, PinnedCompendiumEntryFilter> implements PinnedCompendiumEntryDAO {

}
