package com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.exploration;

import com.jeroensteenbeeke.hyperion.heinlein.web.components.ButtonType;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.dao.DungeonMasterNoteDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.DungeonMasterNote;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMModalWindow;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewCallback;
import org.apache.wicket.markup.html.basic.MultiLineLabel;

import javax.inject.Inject;

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
