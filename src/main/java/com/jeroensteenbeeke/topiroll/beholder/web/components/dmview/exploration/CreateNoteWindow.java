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
import com.jeroensteenbeeke.topiroll.beholder.beans.NoteService;
import com.jeroensteenbeeke.topiroll.beholder.entities.DungeonMasterNote;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMModalWindow;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewCallback;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.PatternValidator;

import jakarta.inject.Inject;
import java.awt.*;

public class CreateNoteWindow extends DMModalWindow<ScaledMap> {
	private static final long serialVersionUID = 5749469936570088653L;

	@Inject
	private NoteService noteService;

	public CreateNoteWindow(String id, ScaledMap map, DMViewCallback callback) {
		super(id, ModelMaker.wrap(map), "Create Note");

		final Point point = callback.getClickedLocation().orElseThrow(CannotCreateModalWindowException::new);

		final int x = point.x;
		final int y = point.y;

		TextArea<String> textArea = new TextArea<>("text", Model.of(""));
		TextField<String> colorField = new TextField<>("color", Model.of("")) {
			private static final long serialVersionUID = -5003631731986321641L;

			@Override
			protected String[] getInputTypes() {
				return new String[] { "color" };
			}
		};
		colorField.add(new PatternValidator("^(|#[0-9a-fA-F]{6})$"));

		Form<DungeonMasterNote> form = new Form<>("form") {

			private static final long serialVersionUID = 754754814444484876L;

			@Override
			protected void onSubmit() {
				super.onSubmit();

				noteService.createNote(CreateNoteWindow.this.getModelObject(), x, y, colorField.getModelObject(), textArea.getModelObject());
			}
		};
		form.add(textArea);
		form.add(colorField);

		add(form);

		addAjaxSubmitButton(target -> {
			setVisible(false);

			target.add(CreateNoteWindow.this);
			target.appendJavaScript("$('#combat-modal').modal('hide');");

			callback.redrawMap(target);
			callback.removeModal(target);
		}).forForm(form).ofType(ButtonType.Primary).withLabel("Add note");
	}
}
