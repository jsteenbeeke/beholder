package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.preparation;

import com.jeroensteenbeeke.hyperion.heinlein.web.components.BootstrapPagingNavigator;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.IconLink;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.entity.BSEntityFormPage;
import com.jeroensteenbeeke.hyperion.icons.fontawesome.FontAwesome;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.topiroll.beholder.beans.AmazonS3Service;
import com.jeroensteenbeeke.topiroll.beholder.dao.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.YouTubePlaylist;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.YouTubePlaylistFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.AuthenticatedPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.PrepareSessionPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;

import javax.inject.Inject;

public class PrepareMusicPage extends AuthenticatedPage {

	private static final long serialVersionUID = 1L;


	@Inject
	private MapViewDAO mapViewDAO;

	@Inject
	private TokenDefinitionDAO tokenDAO;

	@Inject
	private MapFolderDAO mapFolderDAO;

	@Inject
	private PortraitDAO portraitDAO;

	@Inject
	private YouTubePlaylistDAO playlistDAO;

	@Inject
	private AmazonS3Service amazon;

	public PrepareMusicPage() {
		super("Prepare music");

		YouTubePlaylistFilter playlistFilter = new YouTubePlaylistFilter();
		playlistFilter.owner(getUser()).name().orderBy(true);

		DataView<YouTubePlaylist> playlistView = new DataView<YouTubePlaylist>("playlists",
				FilterDataProvider.of(playlistFilter, playlistDAO)) {
			@Override
			protected void populateItem(Item<YouTubePlaylist> item) {
				YouTubePlaylist playlist = item.getModelObject();

				item.add(new Label("name", playlist.getName()));
				item.add(new ExternalLink("url", playlist.getUrl())
						.setBody(Model.of(playlist.getUrl())));
				item.add(new IconLink<YouTubePlaylist>("edit", item.getModel(), FontAwesome.edit) {
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

							@Override
							protected void onDeleted() {
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

		add(new Link<Void>("back") {
			@Override
			public void onClick() {
				setResponsePage(new PrepareSessionPage());
			}
		});
	}
}
