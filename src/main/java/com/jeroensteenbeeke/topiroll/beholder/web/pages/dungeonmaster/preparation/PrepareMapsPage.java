package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.preparation;

import com.jeroensteenbeeke.hyperion.heinlein.web.pages.entity.BSEntityFormPage;
import com.jeroensteenbeeke.topiroll.beholder.dao.MapFolderDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapFolder;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.MapFolderFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.ScaledMapFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.components.MapOverviewPanel;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.AuthenticatedPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.PrepareSessionPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.UploadMapStep1Page;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.ViewFolderPage;
import org.apache.wicket.markup.html.link.Link;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class PrepareMapsPage extends AuthenticatedPage {

	private static final long serialVersionUID = 1L;


	@Inject
	private MapFolderDAO mapFolderDAO;


	public PrepareMapsPage() {
		super("Prepare maps");


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


		add(new Link<ScaledMap>("addmap") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new UploadMapStep1Page(null));

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

		add(new Link<Void>("back") {
			@Override
			public void onClick() {
				setResponsePage(new PrepareSessionPage());
			}
		});
	}

}
