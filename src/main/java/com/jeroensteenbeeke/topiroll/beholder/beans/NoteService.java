package com.jeroensteenbeeke.topiroll.beholder.beans;

import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;

public interface NoteService {

	void createNote(ScaledMap map, int x, int y, String text);

}
