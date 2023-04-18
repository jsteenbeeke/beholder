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
package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster;

import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapFolder;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.preparation.PrepareMapsPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;

import org.jetbrains.annotations.Nullable;
import java.util.LinkedList;

public class UploadMapStep1Page extends AuthenticatedPage {
	private static final long serialVersionUID = 1L;

	private IModel<MapFolder> folderModel;

	public UploadMapStep1Page(@Nullable MapFolder folder) {
		super("Upload Map");

		if (folder != null) {
			folderModel = ModelMaker.wrap(folder);
		} else {
			folderModel = Model.of((MapFolder) null);
		}

		add(new Link<BeholderUser>("back") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new PrepareMapsPage());

			}
		});

		FileUploadField uploadField = new FileUploadField("file",
				new ListModel<>(new LinkedList<>()));
		uploadField.setRequired(true);

		Form<ScaledMap> uploadForm = new Form<ScaledMap>("uploadForm") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				FileUpload upload = uploadField.getFileUpload();

				try {
					setResponsePage(new UploadMapStep2Page(upload.writeToTempFile(),
							upload.getClientFileName(), folderModel));

				} catch (Exception e) {
					error(String.format("Could not convert file input: %s",
							e.getMessage()));
				}

			}

		};
		uploadForm.setMultiPart(true);
		uploadForm.add(uploadField);

		add(uploadForm);

		add(new SubmitLink("submit", uploadForm));
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		folderModel.detach();
	}
}
