/**
 * This file is part of Beholder
 * (C) 2016 Jeroen Steenbeeke
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

import com.jeroensteenbeeke.hyperion.util.ImageUtil;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.util.ListModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

public class UploadPortraitStep1Page extends AuthenticatedPage {
	private static final long serialVersionUID = 1L;

	public UploadPortraitStep1Page() {
		super("Upload Portrait");

		add(new Link<BeholderUser>("back") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new PrepareSessionPage());

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
					ByteArrayOutputStream bos = new ByteArrayOutputStream();

					int b;

					InputStream in = upload.getInputStream();
					while ((b = in.read()) != -1) {
						bos.write(b);

					}

					bos.flush();
					bos.close();

					byte[] image = bos.toByteArray();

					if (ImageUtil.isWebImage(image)) {
						setResponsePage(new UploadPortraitStep2Page(image,
								upload.getClientFileName()));
					} else {
						error("Unrecognized image format");
					}

				} catch (IOException e) {
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

}
