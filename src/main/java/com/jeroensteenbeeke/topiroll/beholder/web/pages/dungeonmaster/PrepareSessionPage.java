package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster;

import com.jeroensteenbeeke.hyperion.heinlein.web.components.BootstrapPagingNavigator;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.GlyphIcon;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.IconLink;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.BSEntityFormPage;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.BSEntityPageSettings;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.ConfirmationPage;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.hyperion.util.ActionResult;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.*;
import com.jeroensteenbeeke.topiroll.beholder.web.components.MapOverviewPanel;
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


	protected static final double TOKEN_THUMB_MAX = 40;

	@Inject
	private MapViewDAO mapViewDAO;

	@Inject
	private ScaledMapDAO mapDAO;

	@Inject
	private TokenDefinitionDAO tokenDAO;

	@Inject
	private MapFolderDAO mapFolderDAO;

	@Inject
	private PortraitDAO portraitDAO;

	@Inject
	private YouTubePlaylistDAO playlistDAO;

	public PrepareSessionPage() {
		super("");

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

		add(new MapOverviewPanel("maps", getUser()) {
			@Override
			protected void decorateFolderFilter(
					@Nonnull
							MapFolderFilter folderFilter) {
				folderFilter.parent().isNull();
			}

			@Override
			protected void decorateMapFilter(
					@Nonnull
							ScaledMapFilter mapFilter) {
				mapFilter.folder().isNull();
			}
		});

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
				item.add(new ContextImage("thumb",
						"images/token/" + definition.getId()));
				item.add(new IconLink<TokenDefinition>("edit", item.getModel(),
						GlyphIcon.edit) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						TokenDefinition tokenDefinition = getModelObject();

						BSEntityPageSettings<TokenDefinition> settings =
								tokenDefinition.getInstances().isEmpty() ?
										edit(tokenDefinition).onPage("Edit Token")
															 .using(tokenDAO) :
										edit(tokenDefinition).onPage("Edit Token").withoutDelete()
															 .using(tokenDAO);
						setResponsePage(new BSEntityFormPage<TokenDefinition>(
								settings) {

							private static final long serialVersionUID = 1L;

							@Override
							protected void onSaved(TokenDefinition entity) {
								setResponsePage(new PrepareSessionPage());

							}

							@Override
							protected void onCancel(TokenDefinition entity) {
								setResponsePage(new PrepareSessionPage());
							}

						});

					}
				});

			}

		};

		tokenView.setItemsPerPage(25);
		add(tokenView);
		add(new BootstrapPagingNavigator("tokennav", tokenView));


		DataView<Portrait> portraitView = new DataView<Portrait>("portraits",
				FilterDataProvider.of(new PortraitFilter().owner(getUser()).name().orderBy(true),
						portraitDAO)) {
			@Override
			protected void populateItem(Item<Portrait> item) {
				Portrait portrait = item.getModelObject();

				item.add(new Label("name", portrait.getName()));
				final Blob imageData = portrait.getData();

				item.add(new ContextImage("thumb","images/portrait/"+ portrait.getId()));
				item.add(new IconLink<Portrait>("edit", item.getModel(),
						GlyphIcon.edit) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						setResponsePage(new BSEntityFormPage<Portrait>(
								edit(getModelObject()).onPage("Edit Portrait")
													  .using(portraitDAO)) {

							private static final long serialVersionUID = 1L;

							@Override
							protected void onSaved(Portrait entity) {
								setResponsePage(new PrepareSessionPage());

							}

							@Override
							protected void onCancel(Portrait entity) {
								setResponsePage(new PrepareSessionPage());
							}

						});

					}
				});
			}
		};
		portraitView.setItemsPerPage(25);
		add(portraitView);
		add(new BootstrapPagingNavigator("portraitnav", portraitView));

		DataView<YouTubePlaylist> playlistView = new DataView<YouTubePlaylist>("playlists",
				FilterDataProvider
						.of(new YouTubePlaylistFilter().owner(getUser()).name().orderBy(true),
								playlistDAO)) {
			@Override
			protected void populateItem(Item<YouTubePlaylist> item) {
				YouTubePlaylist playlist = item.getModelObject();

				item.add(new Label("name", playlist.getName()));
				item.add(new ExternalLink("url", playlist.getUrl())
						.setBody(Model.of(playlist.getUrl())));
				item.add(new IconLink<YouTubePlaylist>("edit", item.getModel(), GlyphIcon.edit) {
					@Override
					public void onClick() {
						setResponsePage(new BSEntityFormPage<YouTubePlaylist>(
								edit(getModelObject()).onPage("Edit Playlist").using
										(playlistDAO)) {

							@Override
							protected void onSaved(YouTubePlaylist entity) {
								setResponsePage(new PrepareSessionPage());
							}

							@Override
							protected void onCancel(YouTubePlaylist entity) {
								setResponsePage(new PrepareSessionPage());
							}
						});
					}
				});

			}
		};
		playlistView.setItemsPerPage(25);
		add(playlistView);
		add(new BootstrapPagingNavigator("playlistnav", playlistView));

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

		add(new Link<ScaledMap>("addmap") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new UploadMapStep1Page(null));

			}
		});

		add(new Link<TokenDefinition>("addtoken") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new UploadTokenStep1Page());

			}
		});

		add(new Link<MapFolder>("addfolder") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new BSEntityFormPage<MapFolder>(
						create(new MapFolder()).onPage("Create Folder").using(mapFolderDAO)) {

					@Override
					protected void onSaved(MapFolder entity) {
						setResponsePage(new ViewFolderPage(entity));
					}

					@Override
					protected void onCancel(MapFolder entity) {
						setResponsePage(new PrepareSessionPage());
					}
				});
			}
		});

		add(new Link<MapFolder>("addportrait") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new UploadPortraitStep1Page());
			}
		});

		add(new Link<YouTubePlaylist>("addplaylist") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new BSEntityFormPage<YouTubePlaylist>(
						create(new YouTubePlaylist()).onPage("Add Playlist").using(playlistDAO)) {

					@Override
					protected void onBeforeSave(YouTubePlaylist entity) {
						super.onBeforeSave(entity);
						entity.setOwner(getUser());
					}

					@Override
					protected void onSaved(YouTubePlaylist entity) {
						setResponsePage(new PrepareSessionPage());
					}

					@Override
					protected void onCancel(YouTubePlaylist entity) {
						setResponsePage(new PrepareSessionPage());
					}
				});
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
