package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import com.jeroensteenbeeke.hyperion.ducktape.web.resources.ThumbnailResource;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.BootstrapPagingNavigator;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.GlyphIcon;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.IconLink;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.BSEntityFormPage;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.dao.MapFolderDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.ScaledMapDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.TokenDefinitionDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapFolder;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.MapFolderFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.ScaledMapFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.components.MapOverviewPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

public class ViewFolderPage extends AuthenticatedPage {

	private static final long serialVersionUID = 1L;

	@Inject
	private ScaledMapDAO mapDAO;

	@Inject
	private MapFolderDAO mapFolderDAO;

	private IModel<MapFolder> folderModel;

	public  ViewFolderPage(@Nonnull MapFolder folder) {
		super("View folder - ".concat(folder.getName()));

		this.folderModel = ModelMaker.wrap(folder);

		add(new MapOverviewPanel("maps", getUser()) {
			@Override
			protected void decorateFolderFilter(@Nonnull MapFolderFilter folderFilter) {
				folderFilter.parent(folderModel.getObject());
			}

			@Override
			protected void decorateMapFilter(@Nonnull ScaledMapFilter mapFilter) {
				mapFilter.folder(folderModel.getObject());
			}
		});

		add(new Link<ScaledMap>("back") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				MapFolder parent = folderModel.getObject().getParent();
				if (parent == null) {
					setResponsePage(new PrepareSessionPage());
				} else {
					setResponsePage(new ViewFolderPage(parent));
				}

			}
		});

		add(new Link<ScaledMap>("addmap") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new UploadMapStep1Page());

			}
		});

		add(new Link<MapFolder>("addfolder") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new BSEntityFormPage<MapFolder>(create(new MapFolder()).onPage("Create Folder").using(mapFolderDAO)) {


					@Override
					protected void onBeforeSave(MapFolder entity) {
						super.onBeforeSave(entity);
						entity.setParent(folderModel.getObject());
					}

					@Override
					protected void onSaved(MapFolder entity) {
						setResponsePage(new ViewFolderPage(folderModel.getObject()));
					}

					@Override
					protected void onCancel(MapFolder entity) {
						setResponsePage(new ViewFolderPage(folderModel.getObject()));
					}
				});
			}
		});
	}

	@Override
	protected void onDetach() {
		super.onDetach();

		folderModel.detach();
	}
}
