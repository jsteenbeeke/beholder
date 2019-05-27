package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.preparation;

import com.jeroensteenbeeke.hyperion.heinlein.web.components.BootstrapPagingNavigator;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.IconLink;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.entity.BSEntityFormPage;
import com.jeroensteenbeeke.hyperion.icons.fontawesome.FontAwesome;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.lux.ActionResult;
import com.jeroensteenbeeke.topiroll.beholder.beans.RemoteImageService;
import com.jeroensteenbeeke.topiroll.beholder.dao.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapFolder;
import com.jeroensteenbeeke.topiroll.beholder.entities.Portrait;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.PortraitFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.AuthenticatedPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.PrepareSessionPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.UploadPortraitStep1Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;

import javax.inject.Inject;

public class PreparePortraitsPage extends AuthenticatedPage {

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
	private RemoteImageService amazon;

	public PreparePortraitsPage() {
		super("Prepare portraits");


		PortraitFilter portraitFilter = new PortraitFilter();
		portraitFilter.owner(getUser());
		portraitFilter.name().orderBy(true);

		DataView<Portrait> portraitView = new DataView<Portrait>("portraits",
				FilterDataProvider.of(portraitFilter, portraitDAO)) {
			private static final long serialVersionUID = 5900594289239505431L;

			@Override
			protected void populateItem(Item<Portrait> item) {
				Portrait portrait = item.getModelObject();

				item.add(new Label("name", portrait.getName()));

				item.add(new ContextImage("thumb", portrait.getImageUrl()));
				item.add(new IconLink<>("edit", item.getModel(), FontAwesome.edit) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						setResponsePage(new BSEntityFormPage<>(
							edit(getModelObject()).onPage("Edit Portrait").using(portraitDAO)) {

							private static final long serialVersionUID = 1L;

							@Override
							protected ActionResult onBeforeDelete(Portrait entity) {
								if (entity.getAmazonKey() != null) {
									return amazon.removeImage(entity.getAmazonKey());
								}

								return ActionResult.ok();
							}

							@Override
							protected void onDeleted() {
								setResponsePage(new PrepareSessionPage());
							}

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

		add(new Link<MapFolder>("addportrait") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new UploadPortraitStep1Page());
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
