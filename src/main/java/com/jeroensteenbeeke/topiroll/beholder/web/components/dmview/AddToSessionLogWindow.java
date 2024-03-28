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
package com.jeroensteenbeeke.topiroll.beholder.web.components.dmview;

import com.jeroensteenbeeke.hyperion.heinlein.web.components.ButtonType;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.SessionLogService;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.web.BeholderSession;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMModalWindow;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewCallback;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.Model;

import jakarta.inject.Inject;

public class AddToSessionLogWindow extends DMModalWindow<ScaledMap> {
	private static final long serialVersionUID = 2854668991094369943L;
	@Inject
	private SessionLogService sessionLogService;

	public AddToSessionLogWindow(String id, ScaledMap map, DMViewCallback callback) {
		super(id, ModelMaker.wrap(map), "Session Log Entry");

		final TextArea<String> entryField = new TextArea<>("entry", Model.of(""));
		entryField.setRequired(true);

		Form<ScaledMap> entryForm = new Form<ScaledMap>("form", ModelMaker.wrap(map)) {
			private static final long serialVersionUID = 5345121083973575122L;

			@Override
			protected void onSubmit() {
				String label = entryField.getModelObject();

				BeholderUser user = BeholderSession.get().getUser();

				if (user != null) {
					sessionLogService.addSessionLogEntry(user, label);
				}
			}
		};

		entryForm.add(entryField);

		add(entryForm);

		addAjaxSubmitButton(target -> {
			setVisible(false);

			target.add(AddToSessionLogWindow.this);
			target.appendJavaScript("$('#combat-modal').modal('hide');");

			callback.redrawMap(target);
			callback.removeModal(target);
		}).forForm(entryForm).ofType(ButtonType.Primary).withLabel("Add entry");
	}
}
