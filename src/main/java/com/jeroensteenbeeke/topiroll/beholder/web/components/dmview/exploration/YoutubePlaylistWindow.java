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
package com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.exploration;

import com.jeroensteenbeeke.hyperion.heinlein.web.components.AjaxIconLink;
import com.jeroensteenbeeke.hyperion.icons.fontawesome.FontAwesome;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.BeholderRegistry;
import com.jeroensteenbeeke.topiroll.beholder.dao.YouTubePlaylistDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.YouTubePlaylist;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.YouTubePlaylistFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMModalWindow;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewCallback;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewPanel;
import com.jeroensteenbeeke.topiroll.beholder.web.data.JSPlaylist;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;

import javax.inject.Inject;
import java.util.Random;

public class YoutubePlaylistWindow extends DMModalWindow<MapView> {
	private static final long serialVersionUID = 6828418250046776290L;
	@Inject
	private YouTubePlaylistDAO playlistDAO;

	public YoutubePlaylistWindow(String id, MapView view, DMViewCallback callback) {
		super(id, ModelMaker.wrap(view), "Playlists");

		YouTubePlaylistFilter playlistFilter = new YouTubePlaylistFilter();
		playlistFilter.owner(view.getOwner()).name().orderBy(true);
		view.getOwner().activeCampaign().peek(c -> playlistFilter.campaign().isNull().orCampaign(c));

		DataView<YouTubePlaylist> playlistView = new DataView<>("playlists",
			FilterDataProvider.of(playlistFilter, playlistDAO)) {
			private static final long serialVersionUID = 5740854845633916486L;

			@Override
			protected void populateItem(Item<YouTubePlaylist> item) {
				YouTubePlaylist playlist = item.getModelObject();

				item.add(new Label("name", playlist.getName()));
				item.add(new AjaxIconLink<>("play", item.getModel(), FontAwesome.play) {
					private static final long serialVersionUID = -5537769023779195576L;

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
							url = url.concat("&amp;index=").concat(Integer
								.toString(new Random().nextInt(playlist.getNumberOfEntries())));
						}

						BeholderRegistry.instance.sendToView(
							YoutubePlaylistWindow.this.getModelObject().getId(),
							r -> !r.isPreviewMode(), new JSPlaylist(url));
					}
				});

			}
		};
		add(playlistView);


		getBody().add(AttributeModifier.replace("style", "height: 300px; overflow: auto;"));
	}
}
