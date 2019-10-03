package com.jeroensteenbeeke.topiroll.beholder.beans.impl;

import com.jeroensteenbeeke.topiroll.beholder.beans.NoteService;
import com.jeroensteenbeeke.topiroll.beholder.dao.DungeonMasterNoteDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.DungeonMasterNote;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope(value = "request")
class NoteServiceImpl implements NoteService {
	@Autowired
	private DungeonMasterNoteDAO noteDAO;

	@Override
	@Transactional
	public void createNote(ScaledMap map, int x, int y, String text) {
		DungeonMasterNote note = new DungeonMasterNote();
		note.setMap(map);
		note.setOffsetX(x);
		note.setOffsetY(y);
		note.setNote(text);
		noteDAO.save(note);
	}
}
