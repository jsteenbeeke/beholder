/*
 * This file is part of Beholder
 * Copyright (C) 2016 - 2023 Jeroen Steenbeeke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.exploration;

import com.jeroensteenbeeke.hyperion.heinlein.web.components.ButtonType;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.dao.DungeonMasterNoteDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.DungeonMasterNote;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMModalWindow;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewCallback;
import org.apache.wicket.markup.html.basic.MultiLineLabel;

import jakarta.inject.Inject;

public class ViewNoteWindow extends DMModalWindow<DungeonMasterNote> {
	private static final long serialVersionUID = 1202138887418133287L;
	@Inject
	private DungeonMasterNoteDAO noteDAO;

	public ViewNoteWindow(String id, DungeonMasterNote note, DMViewCallback callback) {
		super(id, ModelMaker.wrap(note), "View Note");

		add(new MultiLineLabel("note", getModel().map(DungeonMasterNote::getNote)));

		this.<DungeonMasterNote> addAjaxButton((target, n) -> {
			noteDAO.delete(n);

			target.add(ViewNoteWindow.this);
			target.appendJavaScript("$('#combat-modal').modal('hide');");

			callback.redrawMap(target);
			callback.removeModal(target);
		}).withModel(getModel()).ofType(ButtonType.Danger).withLabel("Delete");
	}
}
