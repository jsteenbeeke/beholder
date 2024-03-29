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

import jakarta.inject.Inject;

import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.hyperion.webcomponents.core.form.choice.LambdaRenderer;
import com.jeroensteenbeeke.topiroll.beholder.entities.Campaign;
import com.jeroensteenbeeke.topiroll.beholder.web.model.CampaignsModel;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.preparation.PrepareTokensPage;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.DynamicImageResource;

import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;

public class UploadTokenStep2Page extends AuthenticatedPage {

	private static final long serialVersionUID = 1L;

	public UploadTokenStep2Page(final byte[] image, final String originalName) {
		super("Configure token");

		add(new Link<BeholderUser>("back") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new PrepareTokensPage());

			}
		});

		final TextField<String> nameField = new TextField<>("name",
															Model.of(originalName));
		nameField.setRequired(true);

		final NumberTextField<Integer> sizeField = new NumberTextField<>(
			"size", Model.of(1));
		sizeField.setRequired(true);
		sizeField.setMinimum(1);

		final DropDownChoice<Campaign> campaignChoice = new DropDownChoice<>("campaign", ModelMaker.wrap(Campaign.class), new CampaignsModel(), LambdaRenderer
			.of(Campaign::getName));
		campaignChoice.setRequired(false);
		campaignChoice.setNullValid(true);

		final Image previewImage = new NonCachingImage("preview",
													   new DynamicImageResource() {
														   private static final long serialVersionUID = 1L;

														   @Override
														   protected byte[] getImageData(Attributes attributes) {
															   return image;
														   }
													   });
		previewImage.setOutputMarkupId(true);

		Form<ScaledMap> configureForm = new Form<ScaledMap>("configureForm") {
			private static final long serialVersionUID = 1L;

			@Inject
			private MapService mapService;

			@Override
			protected void onSubmit() {
				user().peek(user -> {
					mapService.createToken(user, campaignChoice.getModelObject(), nameField.getModelObject(),
										   sizeField.getModelObject(), image);

					setResponsePage(new PrepareTokensPage());
				});
			}
		};

		configureForm.add(campaignChoice);
		configureForm.add(sizeField);
		configureForm.add(nameField);

		add(configureForm);

		add(previewImage);

		add(new SubmitLink("submit", configureForm));
	}
}
