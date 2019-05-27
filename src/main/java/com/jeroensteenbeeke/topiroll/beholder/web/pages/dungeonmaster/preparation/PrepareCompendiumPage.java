package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.preparation;

import com.jeroensteenbeeke.hyperion.heinlein.web.components.BootstrapPagingNavigator;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.IconLink;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.ConfirmationPage;
import com.jeroensteenbeeke.hyperion.icons.fontawesome.FontAwesome;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.topiroll.beholder.dao.CompendiumEntryDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.CompendiumEntry;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.CompendiumEntryFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.AuthenticatedPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.PrepareSessionPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;

import javax.inject.Inject;

public class PrepareCompendiumPage extends AuthenticatedPage {
	private final DataView<CompendiumEntry> entryView;

	@Inject
	private CompendiumEntryDAO compendiumEntryDAO;

	public PrepareCompendiumPage() {
		super("User compendium");

		CompendiumEntryFilter filter = new CompendiumEntryFilter();
		filter.author(getUser());
		filter.title().orderBy(true);

		add(entryView = new DataView<>("entries",
			FilterDataProvider.of(filter, compendiumEntryDAO)) {
			private static final long serialVersionUID = -3399820060777345883L;

			@Override
			protected void populateItem(Item<CompendiumEntry> item) {
				item.add(new Label("title", item.getModelObject().getTitle()));
				item.add(new IconLink<>("edit", item.getModel(), FontAwesome.edit) {
					private static final long serialVersionUID = 8837136535113695402L;

					@Override
					public void onClick() {
						setResponsePage(new CompendiumEditorPage(item.getModelObject()));
					}
				});
				item.add(new IconLink<>("delete", item.getModel(),
					FontAwesome.trash) {
					private static final long serialVersionUID = -4536436282162364701L;

					@Override
					public void onClick() {
						setResponsePage(new ConfirmationPage("Confirm deletion",
							String.format(
								"Are you sure you wish to delete the article titled \"%s\"",
								getModelObject().getTitle()), ConfirmationPage.ColorScheme.INVERTED,
							yes -> {
								if (yes) {
									compendiumEntryDAO.delete(item.getModelObject());
								}

								setResponsePage(new PrepareCompendiumPage());
							}));
					}
				});
			}
		});
		add(new BootstrapPagingNavigator("entriesnav", entryView));

		add(new Link<Void>("back") {
			private static final long serialVersionUID = -2560260394292931999L;

			@Override
			public void onClick() {
				setResponsePage(new PrepareSessionPage());
			}
		});

		add(new Link<Void>("addentry") {
			private static final long serialVersionUID = 4337138900456370963L;

			@Override
			public void onClick() {
				setResponsePage(new CompendiumEditorPage(new CompendiumEntry()));
			}
		});
	}
}
