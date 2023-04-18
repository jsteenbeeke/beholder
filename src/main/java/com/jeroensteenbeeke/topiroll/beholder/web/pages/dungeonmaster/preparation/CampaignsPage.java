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

import com.jeroensteenbeeke.hyperion.heinlein.web.components.IconLink;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.IconTextLink;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.ConfirmationPage;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.entity.BSEntityFormPage;
import com.jeroensteenbeeke.hyperion.icons.fontawesome.FontAwesome;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.lux.ActionResult;
import com.jeroensteenbeeke.topiroll.beholder.beans.CampaignService;
import com.jeroensteenbeeke.topiroll.beholder.dao.CampaignDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.entities.Campaign;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.CampaignFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.BeholderSession;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.AuthenticatedPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.PrepareSessionPage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.request.cycle.RequestCycle;

import javax.inject.Inject;

public class CampaignsPage extends AuthenticatedPage {
	private static final long serialVersionUID = 471020722470190200L;

	@Inject
	private CampaignDAO campaignDAO;

	@Inject
	private CampaignService campaignService;

	public CampaignsPage() {
		super("Campaigns");

		CampaignFilter campaignFilter = new CampaignFilter();
		campaignFilter.dungeonMaster(getUser());

		add(new DataView<Campaign>("campaigns", FilterDataProvider.of(campaignFilter, campaignDAO)) {
			private static final long serialVersionUID = -4275677503918714905L;

			@Override
			protected void populateItem(Item<Campaign> item) {
				Campaign campaign = item.getModelObject();

				item.add(new Label("name", item.getModel().map(Campaign::getName)));

				if (user().flatMap(BeholderUser::activeCampaign).filter(campaign::equals).isDefined()) {
					item.add(new Label("active", "Active")
								 .add(AttributeModifier.replace("class", "badge badge-success")));
				} else {
					item.add(new IconTextLink<>("active", item.getModel(),
												FontAwesome.check_circle, c -> "Activate") {
						private static final long serialVersionUID = -8605604961132113879L;

						@Override
						public void onClick() {
							user().map(user -> campaignService
								.setActiveCampaign(user, getModelObject())).peek(result -> {
								result.ifOk(() -> {
									// Force reload of active user
									BeholderSession.get().detach();
									RequestCycle.get().setResponsePage(CampaignsPage.class);
								});
								result.ifNotOk(this::error);
							});
						}
					});
				}

				item.add(new IconLink<>("edit", item.getModel(), FontAwesome.edit) {
					private static final long serialVersionUID = 4729687836652050634L;

					@Override
					public void onClick() {
						setResponsePage(new BSEntityFormPage<>(edit(getModelObject())
																   .onPage("Edit Campaign")
																   .withoutDelete()
																   .using(campaignDAO)) {
							private static final long serialVersionUID = 7461087755691288652L;

							@Override
							protected void onSaved(Campaign entity) {
								setResponsePage(new CampaignsPage());
							}

							@Override
							protected void onCancel(Campaign entity) {
								setResponsePage(new CampaignsPage());
							}


						});
					}
				});

				item.add(new IconLink<>("delete", item.getModel(), FontAwesome.trash) {
					private static final long serialVersionUID = 4729687836652050634L;

					@Override
					protected void onConfigure() {
						super.onConfigure();

						setVisibilityAllowed(campaignService.isDeleteAllowed(getModelObject()));
					}

					@Override
					public void onClick() {
						setResponsePage(new ConfirmationPage("Delete Campaign", "Are you sure you wish to delete campaign " + getModelObject()
							.getName(), ConfirmationPage.ColorScheme.INVERTED, answer -> {
							if (answer) {
								ActionResult result = campaignService.deleteCampaign(getModelObject());

								result.ifOk(() -> setResponsePage(new CampaignsPage()));
								result.ifNotOk(this::error);
							} else {
								setResponsePage(new CampaignsPage());
							}
						}));
					}
				});

			}
		});

		add(new Link<Campaign>("add") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {

				setResponsePage(new BSEntityFormPage<>(create(new Campaign())
					.onPage("Create Campaign")
					.using(campaignDAO)) {
					private static final long serialVersionUID = -9001053138608199403L;

					@Override
					protected void onBeforeSave(Campaign entity) {
						super.onBeforeSave(entity);

						entity.setDungeonMaster(user().getOrElseThrow(IllegalAccessError::new));
					}

					@Override
					protected void onSaved(Campaign entity) {
						CampaignsPage.this.setResponsePage(new CampaignsPage());
					}

					@Override
					protected void onCancel(Campaign entity) {
						CampaignsPage.this.setResponsePage(new CampaignsPage());
					}
				});
			}
		});

		add(new Link<Campaign>("deactivate") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				user().peek(campaignService::deactivateCurrentCampaign).peek(u -> setResponsePage(new CampaignsPage()));

			}

			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisibilityAllowed(user().flatMap(BeholderUser::activeCampaign).isDefined());
			}
		});

		add(new Link<Void>("back") {
			private static final long serialVersionUID = 7726834359145906165L;

			@Override
			public void onClick() {
				setResponsePage(new PrepareSessionPage());
			}
		});
	}
}
