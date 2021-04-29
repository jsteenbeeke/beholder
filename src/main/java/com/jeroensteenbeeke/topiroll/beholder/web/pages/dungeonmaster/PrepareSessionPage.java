/**
 * This file is part of Beholder
 * (C) 2016-2019 Jeroen Steenbeeke
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

import com.jeroensteenbeeke.hyperion.heinlein.web.components.BootstrapPagingNavigator;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.IconLink;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.IconTextLink;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.ConfirmationPage;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.entity.BSEntityFormPage;
import com.jeroensteenbeeke.hyperion.icons.fontawesome.FontAwesome;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.lux.ActionResult;
import com.jeroensteenbeeke.topiroll.beholder.beans.CampaignService;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.CampaignDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.MapViewDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.CampaignFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.MapViewFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.preparation.*;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class PrepareSessionPage extends AuthenticatedPage {
	private static final long serialVersionUID = 1L;

	@Inject
	private MapViewDAO mapViewDAO;

	public PrepareSessionPage() {
		super("Prepare Session");


		MapViewFilter viewFilter = new MapViewFilter();
		viewFilter.owner().set(getUser());
		viewFilter.identifier().orderBy(true);

		DataView<MapView> viewView = new DataView<MapView>("views",
				FilterDataProvider.of(viewFilter, mapViewDAO)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<MapView> item) {
				MapView mapView = item.getModelObject();

				item.add(new Label("identifier", mapView.getIdentifier()));
				item.add(new Label("doorbell", mapView.isListenToDoorbell() ? "Yes" : "No"));
				item.add(new Label("showplayers", mapView.isShowPlayers() ? "Yes" : "No"));
				item.add(new Label("width", mapView.getWidth()));
				item.add(new Label("height", mapView.getHeight()));
				item.add(new Label("diagonal",
						mapView.getScreenDiagonalInInches()));
				item.add(new IconLink<>("edit", item.getModel(), FontAwesome.edit) {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						MapView view = getModelObject();
						final String oldIdentifier = view.getIdentifier();

						setResponsePage(new BSEntityFormPage<>(
							edit(view).onPage("Edit View").using(mapViewDAO)) {
							private static final long serialVersionUID = 1L;

							@Nonnull
							@Override
							protected ActionResult validateEntity(@Nonnull MapView entity) {

								ActionResult result = validateMapView(entity,
									!oldIdentifier.equals(entity.getIdentifier()));

								if (!result.isOk()) {
									return result;
								}

								return super.validateEntity(entity);
							}

							@Override
							protected void onSaved(MapView entity) {
								setResponsePage(new PrepareSessionPage());
							}

							@Override
							protected void onCancel(MapView entity) {
								setResponsePage(new PrepareSessionPage());
							}

						});

					}
				});
				item.add(new IconLink<>("delete", item.getModel(), FontAwesome.trash) {

					private static final long serialVersionUID = 1L;

					@Inject
					private MapService mapService;

					@Override
					public void onClick() {
						MapView mapView = item.getModelObject();

						setResponsePage(new ConfirmationPage("Confirm Deletion",
							String.format(
								"Are you sure you wish to delete view \"%s\"",
								mapView.getIdentifier()), ConfirmationPage.ColorScheme.INVERTED, answer -> {
							if (answer) {
								mapService.delete(item.getModelObject());
							}

							setResponsePage(new PrepareSessionPage());
						}));

					}
				});

			}

		};

		viewView.setItemsPerPage(5);
		add(viewView);
		add(new BootstrapPagingNavigator("viewnav", viewView));


		Link<MapView> l;
		add(l = new Link<>("addview") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				MapView view = new MapView();
				view.setWidth(1920);
				view.setHeight(1080);

				setResponsePage(new BSEntityFormPage<>(
					create(view).onPage("Create Map View").using(mapViewDAO)) {
					private static final long serialVersionUID = 1L;

					@Nonnull
					@Override
					protected ActionResult validateEntity(@Nonnull MapView entity) {

						ActionResult result = validateMapView(entity, true);

						if (!result.isOk()) {
							return result;
						}

						return super.validateEntity(entity);
					}

					@Override
					protected void onBeforeSave(MapView entity) {
						entity.setOwner(getUser());
					}

					@Override
					protected void onSaved(MapView entity) {
						setResponsePage(new PrepareSessionPage());

					}

					@Override
					protected void onCancel(MapView entity) {
						setResponsePage(new PrepareSessionPage());
					}

				});

			}
		});

		add(new Link<ScaledMap>("maps") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new PrepareMapsPage());
			}
		});

		add(new Link<TokenDefinition>("tokens") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new PrepareTokensPage());
			}
		});

		add(new Link<MapFolder>("portraits") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new PreparePortraitsPage());
			}
		});

		add(new Link<YouTubePlaylist>("playlists") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new PrepareMusicPage());
			}
		});

		add(new Link<YouTubePlaylist>("compendium") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new PrepareCompendiumPage());
			}
		});

		add(new Link<YouTubePlaylist>("campaigns") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new CampaignsPage());
			}
		});



	}

	private ActionResult validateMapView(MapView entity, boolean checkIdentifier) {
		if (!entity.getIdentifier().matches("[a-zA-Z0-9]+")) {
			return ActionResult.error(
					"Identifiers may only contain alphanumeric characters");
		}

		MapViewFilter filter = new MapViewFilter();
		filter.identifier().set(entity.getIdentifier());

		if (checkIdentifier && mapViewDAO.countByFilter(filter) > 0) {
			return ActionResult.error("Identifier '%s' already in use",
					entity.getIdentifier());
		}

		return ActionResult.ok();
	}
}
