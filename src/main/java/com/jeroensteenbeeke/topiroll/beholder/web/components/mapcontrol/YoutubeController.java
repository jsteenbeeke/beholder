package com.jeroensteenbeeke.topiroll.beholder.web.components.mapcontrol;

import com.jeroensteenbeeke.hyperion.heinlein.web.components.AjaxBootstrapPagingNavigator;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.AjaxIconLink;
import com.jeroensteenbeeke.hyperion.icons.fontawesome.FontAwesome;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.hyperion.webcomponents.core.TypedPanel;
import com.jeroensteenbeeke.topiroll.beholder.BeholderRegistry;
import com.jeroensteenbeeke.topiroll.beholder.dao.YouTubePlaylistDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.YouTubePlaylist;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.YouTubePlaylistFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.data.JSPlaylist;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;

import javax.inject.Inject;
import java.util.Random;

public class YoutubeController extends TypedPanel<MapView> {
	@Inject
	private YouTubePlaylistDAO playlistDAO;

	public YoutubeController(String id, MapView view) {
		super(id, ModelMaker.wrap(view));

		YouTubePlaylistFilter playlistFilter = new YouTubePlaylistFilter();
		playlistFilter.owner(view.getOwner()).name().orderBy(true);

		DataView<YouTubePlaylist> playlistView = new DataView<YouTubePlaylist>("playlists",
				FilterDataProvider.of(playlistFilter,
						playlistDAO)) {
			@Override
			protected void populateItem(Item<YouTubePlaylist> item) {
				YouTubePlaylist playlist = item.getModelObject();

				item.add(new Label("name", playlist.getName()));
				item.add(new AjaxIconLink<YouTubePlaylist>("play", item.getModel(), FontAwesome.play) {
					@Override
					public void onClick(AjaxRequestTarget target) {
						YouTubePlaylist playlist = getModelObject();
						String url = playlist.getUrl();

						if (!url.contains("autoplay=")) {
							url = url.concat("&amp;autoplay=1");
						}

						if (!url.contains("loop=")) {
							url = url.concat("&amp;loop=1");
						}

						if (playlist.getNumberOfEntries() != null) {
							url = url.concat("&amp;index=")
									.concat(Integer.toString(new Random().nextInt(playlist.getNumberOfEntries())));
						}

						BeholderRegistry.instance
								.sendToView(YoutubeController.this.getModelObject().getId(), r -> !r.isPreviewMode(),
										new JSPlaylist(url));
					}
				});

			}
		};
		playlistView.setItemsPerPage(25);
		add(playlistView);
		add(new AjaxBootstrapPagingNavigator("playlistnav", playlistView));

	}
}
