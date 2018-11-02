package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.preparation;

import com.jeroensteenbeeke.hyperion.heinlein.web.components.BootstrapPagingNavigator;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.GlyphIcon;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.IconLink;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.BSEntityFormPage;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.BSEntityPageSettings;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.ConfirmationPage;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.hyperion.util.ActionResult;
import com.jeroensteenbeeke.topiroll.beholder.beans.AmazonS3Service;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.*;
import com.jeroensteenbeeke.topiroll.beholder.web.components.MapOverviewPanel;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.*;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.UrlUtils;
import org.apache.wicket.request.cycle.RequestCycle;

import javax.annotation.Nonnull;
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
	private AmazonS3Service amazon;

	public PreparePortraitsPage() {
		super("Prepare portraits");



		DataView<Portrait> portraitView = new DataView<Portrait>("portraits",
				FilterDataProvider.of(new PortraitFilter().owner(getUser()).name().orderBy(true),
						portraitDAO)) {
			@Override
			protected void populateItem(Item<Portrait> item) {
				Portrait portrait = item.getModelObject();

				item.add(new Label("name", portrait.getName()));

				item.add(new ContextImage("thumb", portrait.getImageUrl()));
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
			@Override
			public void onClick() {
				setResponsePage(new PrepareSessionPage());
			}
		});
	}
}
