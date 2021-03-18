package com.jeroensteenbeeke.topiroll.beholder.beans;

import com.jeroensteenbeeke.topiroll.beholder.dao.DungeonMasterNoteDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.DungeonMasterNote;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope(value = "request")
public class NoteService {
	@Autowired
	private DungeonMasterNoteDAO noteDAO;

	@Transactional
	public void createNote(ScaledMap map, int x, int y, String color, String text) {
		DungeonMasterNote note = new DungeonMasterNote();
		note.setMap(map);
		note.setOffsetX(x);
		note.setOffsetY(y);
		note.setNote(text);
		note.setColor(color.startsWith("#") ? color.substring(1) : color);
		noteDAO.save(note);
	}
}
