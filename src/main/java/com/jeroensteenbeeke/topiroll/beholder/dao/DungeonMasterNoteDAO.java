package com.jeroensteenbeeke.topiroll.beholder.dao;

import com.jeroensteenbeeke.hyperion.solstice.data.HibernateDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.DungeonMasterNote;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.DungeonMasterNoteFilter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

@Repository
@Scope(value = "request")
public class DungeonMasterNoteDAO
		extends HibernateDAO<DungeonMasterNote, DungeonMasterNoteFilter> {
}
