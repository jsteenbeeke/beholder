package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.preparation;

import com.jeroensteenbeeke.hyperion.heinlein.web.components.BootstrapPagingNavigator;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.IconLink;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.entity.BSEntityFormPage;
import com.jeroensteenbeeke.hyperion.icons.fontawesome.FontAwesome;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.hyperion.webcomponents.core.form.choice.LambdaRenderer;
import com.jeroensteenbeeke.topiroll.beholder.beans.RemoteImageService;
import com.jeroensteenbeeke.topiroll.beholder.dao.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.entities.Campaign;
import com.jeroensteenbeeke.topiroll.beholder.entities.YouTubePlaylist;
import com.jeroensteenbeeke.topiroll.beholder.entities.YouTubePlaylist_;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.CampaignFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.YouTubePlaylistFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.model.CampaignsModel;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.AuthenticatedPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.PrepareSessionPage;
import io.vavr.control.Option;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

import javax.inject.Inject;
import java.util.List;

public class PrepareMusicPage extends AuthenticatedPage {

	private static final long serialVersionUID = 1L;


	@Inject
	private YouTubePlaylistDAO playlistDAO;

	public PrepareMusicPage() {
		super("Prepare music");

		YouTubePlaylistFilter playlistFilter = new YouTubePlaylistFilter();
		playlistFilter.owner(getUser()).name().orderBy(true);

		Option<Campaign> activeCampaign = user().flatMap(BeholderUser::activeCampaign);
		if (activeCampaign.isDefined()) {
			warn(String.format("Only showing playlists that are tied to the currently active campaign (%s) or not campaign-specific", activeCampaign.map(Campaign::getName).get()));
			playlistFilter.campaign().isNull();
			playlistFilter.orCampaign(activeCampaign.get());
		}

		playlistFilter.campaign().orderBy(true);
		playlistFilter.name().orderBy(true);

		DataView<YouTubePlaylist> playlistView = new DataView<YouTubePlaylist>("playlists",
																			   FilterDataProvider.of(playlistFilter, playlistDAO)) {
			private static final long serialVersionUID = 2688306082997628230L;

			@Override
			protected void populateItem(Item<YouTubePlaylist> item) {
				YouTubePlaylist playlist = item.getModelObject();

				item.add(new Label("name", playlist.getName()));
				item.add(new Label("campaign", item
					.getModel()
					.map(YouTubePlaylist::getCampaign)
					.map(Campaign::getName)
					.orElse("-")));
				item.add(new ExternalLink("url", playlist.getUrl())
							 .setBody(Model.of(playlist.getUrl())));
				item.add(new IconLink<>("edit", item.getModel(), FontAwesome.edit) {
					private static final long serialVersionUID = -2793136258714671274L;

					@Override
					public void onClick() {
						setResponsePage(new BSEntityFormPage<>(
							edit(getModelObject()).onPage("Edit Playlist").using(playlistDAO)) {

							private static final long serialVersionUID = 8710021909444986625L;

							@Override
							protected void onSaved(YouTubePlaylist entity) {
								setResponsePage(new PrepareMusicPage());
							}

							@Override
							protected void onCancel(YouTubePlaylist entity) {
								setResponsePage(new PrepareMusicPage());
							}

							@Override
							protected void onDeleted() {
								setResponsePage(new PrepareSessionPage());
							}
						}
											.setChoicesModel(YouTubePlaylist_.campaign, new CampaignsModel())
											.setRenderer(YouTubePlaylist_.campaign, LambdaRenderer.of(Campaign::getName)));
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
				setResponsePage(new BSEntityFormPage<>(
					create(new YouTubePlaylist()).onPage("Add Playlist").using(playlistDAO)) {

					private static final long serialVersionUID = 3409766013597313156L;

					@Override
					protected void onBeforeSave(YouTubePlaylist entity) {
						super.onBeforeSave(entity);
						user().peek(entity::setOwner);
					}

					@Override
					protected void onSaved(YouTubePlaylist entity) {
						setResponsePage(new PrepareMusicPage());
					}

					@Override
					protected void onCancel(YouTubePlaylist entity) {
						setResponsePage(new PrepareMusicPage());
					}

				}
									.setChoicesModel(YouTubePlaylist_.campaign, new CampaignsModel())
									.setRenderer(YouTubePlaylist_.campaign, LambdaRenderer.of(Campaign::getName)));
			}
		});

		add(new Link<Void>("back") {
			private static final long serialVersionUID = 6576205835523219034L;

			@Override
			public void onClick() {
				setResponsePage(new PrepareSessionPage());
			}
		});
	}
}
