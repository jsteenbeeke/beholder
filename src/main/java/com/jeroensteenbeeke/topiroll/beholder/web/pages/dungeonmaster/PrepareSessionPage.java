package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster;

import com.jeroensteenbeeke.hyperion.heinlein.web.components.BootstrapPagingNavigator;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.GlyphIcon;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.IconLink;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.BSEntityFormPage;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.BSEntityPageSettings;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.ConfirmationPage;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.lux.ActionResult;
import com.jeroensteenbeeke.topiroll.beholder.beans.AmazonS3Service;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.*;
import com.jeroensteenbeeke.topiroll.beholder.web.components.MapOverviewPanel;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.preparation.*;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.UrlUtils;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.ContextRelativeResource;
import org.apache.wicket.request.resource.ResourceStreamResource;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.sql.Blob;

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
				final String url = UrlUtils.rewriteToContextRelative(
						String.format("views/%s", mapView.getIdentifier()),
						RequestCycle.get());

				item.add(new Label("width", mapView.getWidth()));
				item.add(new Label("height", mapView.getHeight()));
				item.add(new Label("diagonal",
						mapView.getScreenDiagonalInInches()));
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

								ActionResult result =
										validateMapView(entity,
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
								ConfirmationPage.ColorScheme.INVERTED, answer -> {
							if (answer) {
								mapService
										.delete(item.getModelObject());
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
