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
package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.preparation;

import com.jeroensteenbeeke.hyperion.heinlein.web.components.BootstrapPagingNavigator;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.IconLink;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.entity.BSEntityFormPage;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.entity.BSEntityPageSettings;
import com.jeroensteenbeeke.hyperion.icons.fontawesome.FontAwesome;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.hyperion.webcomponents.core.form.choice.LambdaRenderer;
import com.jeroensteenbeeke.lux.ActionResult;
import com.jeroensteenbeeke.topiroll.beholder.beans.RemoteImageService;
import com.jeroensteenbeeke.topiroll.beholder.dao.TokenDefinitionDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.TokenDefinitionFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.model.CampaignsModel;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.AuthenticatedPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.PrepareSessionPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.UploadTokenStep1Page;
import io.vavr.control.Option;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;

import jakarta.inject.Inject;

public class PrepareTokensPage extends AuthenticatedPage {

	private static final long serialVersionUID = 1L;


	@Inject
	private TokenDefinitionDAO tokenDAO;

	@Inject
	private RemoteImageService amazon;

	public PrepareTokensPage() {
		super("Prepare tokens");

		TokenDefinitionFilter tokenFilter = new TokenDefinitionFilter();
		tokenFilter.owner().set(getUser());

		Option<Campaign> activeCampaign = user().flatMap(BeholderUser::activeCampaign);
		if (activeCampaign.isDefined()) {
			warn(String.format("Only showing tokens that are tied to the currently active campaign (%s) or not campaign-specific", activeCampaign.map(Campaign::getName).get()));
			tokenFilter.campaign().isNull();
			tokenFilter.orCampaign(activeCampaign.get());
		}

		tokenFilter.name().orderBy(true);

		DataView<TokenDefinition> tokenView = new DataView<TokenDefinition>(
				"tokens", FilterDataProvider.of(tokenFilter, tokenDAO)) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<TokenDefinition> item) {
				TokenDefinition definition = item.getModelObject();

				item.add(new Label("name", definition.getName()));
				item.add(new Label("campaign", item.getModel().map(TokenDefinition::getCampaign).map(Campaign::getName).orElse("-")));
				item.add(
						new Label("size", String.format("%d squares (diameter)",
								definition.getDiameterInSquares())));
				item.add(new ContextImage("thumb",
						definition.getImageUrl()));
				item.add(new IconLink<>("edit", item.getModel(), FontAwesome.edit) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						TokenDefinition tokenDefinition = getModelObject();

						BSEntityPageSettings<TokenDefinition> settings =
								tokenDefinition.getInstances().isEmpty() ?
									edit(tokenDefinition).onPage("Edit Token").using(tokenDAO) :
									edit(tokenDefinition).onPage("Edit Token").withoutDelete().using(tokenDAO);
						setResponsePage(new BSEntityFormPage<>(settings) {

							private static final long serialVersionUID = 1L;

							@Override
							protected ActionResult onBeforeDelete(
								TokenDefinition entity) {
								if (entity.getAmazonKey() != null) {
									return amazon.removeImage(entity.getAmazonKey());
								}

								return ActionResult.ok();
							}

							@Override
							protected void onDeleted() {
								setResponsePage(new PrepareTokensPage());
							}

							@Override
							protected void onSaved(TokenDefinition entity) {
								setResponsePage(new PrepareTokensPage());

							}

							@Override
							protected void onCancel(TokenDefinition entity) {
								setResponsePage(new PrepareTokensPage());
							}

						}.setChoicesModel(TokenDefinition_.campaign, new CampaignsModel())
						 .setRenderer(TokenDefinition_.campaign, LambdaRenderer.of(Campaign::getName)));

					}
				});

			}

		};

		tokenView.setItemsPerPage(25);
		add(tokenView);
		add(new BootstrapPagingNavigator("tokennav", tokenView));

		add(new Link<TokenDefinition>("addtoken") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new UploadTokenStep1Page());

			}
		});

		add(new Link<Void>("back") {
			private static final long serialVersionUID = -8753319554186657206L;

			@Override
			public void onClick() {
				setResponsePage(new PrepareSessionPage());
			}
		});

	}
}
