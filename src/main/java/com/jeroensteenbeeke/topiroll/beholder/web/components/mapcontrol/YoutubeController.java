package com.jeroensteenbeeke.topiroll.beholder.web.components.mapcontrol;

import com.jeroensteenbeeke.hyperion.ducktape.web.components.TypedPanel;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.*;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.BSEntityFormPage;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.BeholderRegistry;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.YouTubePlaylistDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.YouTubePlaylist;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.YouTubePlaylistFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.data.JSPlaylist;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.PrepareSessionPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;

import javax.inject.Inject;
import java.util.Random;

public class YoutubeController extends TypedPanel<MapView> {
	@Inject
	private YouTubePlaylistDAO playlistDAO;

	public YoutubeController(String id, MapView view) {
		super(id, ModelMaker.wrap(view));

		DataView<YouTubePlaylist> playlistView = new DataView<YouTubePlaylist>("playlists",
				FilterDataProvider.of(new YouTubePlaylistFilter().owner(view.getOwner()).name().orderBy(true),
						playlistDAO)) {
			@Override
			protected void populateItem(Item<YouTubePlaylist> item) {
				YouTubePlaylist playlist = item.getModelObject();

				item.add(new Label("name", playlist.getName()));
				item.add(new AjaxIconLink<YouTubePlaylist>("play", item.getModel(), GlyphIcon.play) {
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
