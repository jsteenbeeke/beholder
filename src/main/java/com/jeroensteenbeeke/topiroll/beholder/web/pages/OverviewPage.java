/**
 * This file is part of Beholder
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import java.awt.Dimension;

import javax.inject.Inject;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.UrlUtils;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.ByteArrayResource;

import com.jeroensteenbeeke.hyperion.ducktape.web.resources.ThumbnailResource;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.BootstrapPagingNavigator;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.GlyphIcon;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.IconLink;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.BSEntityFormPage;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.ConfirmationPage;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.ConfirmationPage.ColorScheme;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.hyperion.util.ActionResult;
import com.jeroensteenbeeke.hyperion.util.ImageUtil;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.MapViewDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.ScaledMapDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.TokenDefinitionDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenDefinition;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.MapViewFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.ScaledMapFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.TokenDefinitionFilter;

public class OverviewPage extends AuthenticatedPage {

	private static final long serialVersionUID = 1L;

	protected static final double TOKEN_THUMB_MAX = 40;

	@Inject
	private MapViewDAO mapViewDAO;

	@Inject
	private ScaledMapDAO mapDAO;

	@Inject
	private TokenDefinitionDAO tokenDAO;

	public OverviewPage() {
		super("Overview");

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
				final String url = UrlUtils.rewriteToContextRelative(
						String.format("views/%s", mapView.getIdentifier()),
						RequestCycle.get());

				item.add(new ExternalLink("url", url).setBody(Model.of(url))
						.add(AttributeModifier.replace("target", "_blank")));
				item.add(new Label("width", mapView.getWidth()));
				item.add(new Label("height", mapView.getHeight()));
				item.add(new Label("diagonal",
						mapView.getScreenDiagonalInInches()));
				item.add(new IconLink<MapView>("control", item.getModel(),
						GlyphIcon.eyeOpen) {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						setResponsePage(
								new ControlViewPage(item.getModelObject()));

					}
				});
				item.add(new IconLink<MapView>("edit", item.getModel(),
						GlyphIcon.edit) {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						MapView view = getModelObject();
						final String oldIdentifier = view.getIdentifier();
						
						setResponsePage(new BSEntityFormPage<MapView>(
								edit(view).onPage("Edit View")
										.using(mapViewDAO)) {
							private static final long serialVersionUID = 1L;

							@Override
							protected ActionResult validateEntity(
									MapView entity) {

								ActionResult result = validateMapView(entity, !oldIdentifier.equals(entity.getIdentifier()));

								if (result != null) {
									return result;
								}

								return super.validateEntity(entity);
							}

							@Override
							protected void onSaved(MapView entity) {
								setResponsePage(new OverviewPage());
							}

							@Override
							protected void onCancel(MapView entity) {
								setResponsePage(new OverviewPage());
							}

						});

					}
				});
				item.add(new IconLink<MapView>("delete", item.getModel(),
						GlyphIcon.trash) {

					private static final long serialVersionUID = 1L;

					@Inject
					private MapService mapService;

					@Override
					public void onClick() {
						MapView mapView = item.getModelObject();

						setResponsePage(new ConfirmationPage("Confirm Deletion",
								String.format(
										"Are you sure you wish to delete view \"%s\"",
										mapView.getIdentifier()),
								ColorScheme.INVERTED, answer -> {
									if (answer) {
										mapService
												.delete(item.getModelObject());
									}

									setResponsePage(new OverviewPage());
								}));

					}
				});

			}

		};

		viewView.setItemsPerPage(5);
		add(viewView);
		add(new BootstrapPagingNavigator("viewnav", viewView));

		ScaledMapFilter mapFilter = new ScaledMapFilter();
		mapFilter.owner().set(getUser());
		mapFilter.name().orderBy(true);

		DataView<ScaledMap> mapView = new DataView<ScaledMap>("maps",
				FilterDataProvider.of(mapFilter, mapDAO)) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<ScaledMap> item) {
				ScaledMap map = item.getModelObject();

				item.add(new Label("name", map.getName()));
				item.add(new NonCachingImage("thumb",
						new ThumbnailResource(128, map.getData())));
				item.add(new IconLink<ScaledMap>("view", item.getModel(),
						GlyphIcon.eyeOpen) {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						setResponsePage(new ViewMapPage(item.getModelObject()));

					}
				});
			}

		};

		mapView.setItemsPerPage(10);
		add(mapView);
		add(new BootstrapPagingNavigator("mapnav", mapView));

		TokenDefinitionFilter tokenFilter = new TokenDefinitionFilter();
		tokenFilter.owner().set(getUser());
		tokenFilter.name().orderBy(true);

		DataView<TokenDefinition> tokenView = new DataView<TokenDefinition>(
				"tokens", FilterDataProvider.of(tokenFilter, tokenDAO)) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<TokenDefinition> item) {
				TokenDefinition definition = item.getModelObject();

				item.add(new Label("name", definition.getName()));
				item.add(
						new Label("size", String.format("%d squares (diameter)",
								definition.getDiameterInSquares())));

				byte[] imageData = definition.getImageData();
				Dimension dimension = ImageUtil.getImageDimensions(imageData);

				if (dimension.getHeight() > TOKEN_THUMB_MAX
						|| dimension.getWidth() > TOKEN_THUMB_MAX) {
					item.add(new Image("thumb",
							new ThumbnailResource(40, imageData)));
				} else {
					item.add(new Image("thumb", new ByteArrayResource(
							ImageUtil.getMimeType(imageData), imageData)));
				}
				item.add(new IconLink<TokenDefinition>("edit", item.getModel(),
						GlyphIcon.edit) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						setResponsePage(new BSEntityFormPage<TokenDefinition>(
								edit(getModelObject()).onPage("Edit Token").withoutDelete()
										.using(tokenDAO)) {

							private static final long serialVersionUID = 1L;

							@Override
							protected void onSaved(TokenDefinition entity) {
								setResponsePage(new OverviewPage());

							}

							@Override
							protected void onCancel(TokenDefinition entity) {
								setResponsePage(new OverviewPage());
							}

						});

					}
				});

			}

		};

		tokenView.setItemsPerPage(10);
		add(tokenView);
		add(new BootstrapPagingNavigator("tokennav", tokenView));

		add(new Link<MapView>("addview") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				MapView view = new MapView();
				view.setWidth(1920);
				view.setHeight(1080);

				setResponsePage(new BSEntityFormPage<MapView>(create(view)
						.onPage("Create Map View").using(mapViewDAO)) {
					private static final long serialVersionUID = 1L;

					@Override
					protected ActionResult validateEntity(MapView entity) {

						ActionResult result = validateMapView(entity, true);

						if (result != null) {
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
						setResponsePage(new OverviewPage());

					}

					@Override
					protected void onCancel(MapView entity) {
						setResponsePage(new OverviewPage());
					}

				});

			}
		});

		add(new Link<ScaledMap>("addmap") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new UploadMapStep1Page());

			}
		});

		add(new Link<TokenDefinition>("addtoken") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new UploadTokenStep1Page());

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

		return null;
	}

}
