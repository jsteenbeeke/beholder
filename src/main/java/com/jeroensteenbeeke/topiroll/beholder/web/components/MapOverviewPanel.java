package com.jeroensteenbeeke.topiroll.beholder.web.components;

import com.jeroensteenbeeke.hyperion.ducktape.web.components.TypedPanel;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.BootstrapPagingNavigator;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.GlyphIcon;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.IconLink;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.hyperion.util.ActionResult;
import com.jeroensteenbeeke.topiroll.beholder.beans.AmazonS3Service;
import com.jeroensteenbeeke.topiroll.beholder.dao.MapFolderDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.ScaledMapDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapFolder;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.MapFolderFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.ScaledMapFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.PrepareSessionPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.ViewFolderPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.ViewMapPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public abstract class MapOverviewPanel extends TypedPanel<MapFolder> {


	private static final long serialVersionUID = 1L;

	@Inject
	private MapFolderDAO folderDAO;

	@Inject
	private ScaledMapDAO mapDAO;

	private IModel<BeholderUser> userModel;

	public MapOverviewPanel(String id, BeholderUser user) {
		super(id);

		this.userModel = ModelMaker.wrap(user);

		MapFolderFilter folderFilter = new MapFolderFilter();
		decorateFolderFilter(folderFilter);
		folderFilter.name().orderBy(true);

		DataView<MapFolder> folderView = new DataView<MapFolder>("folders", FilterDataProvider.of(folderFilter, folderDAO)) {
			@Override
			protected void populateItem(Item<MapFolder> item) {
				final MapFolder folder = item.getModelObject();
				item.add(new Link<MapFolder>("name") {
					@Override
					public void onClick() {
						setResponsePage(new ViewFolderPage(item.getModelObject()));
					}
				}.setBody(Model.of(folder.getName())));
				item.add(new IconLink<MapFolder>("delete", item.getModel(), GlyphIcon.trash) {
					@Override
					public void onClick() {
						MapFolder folder = item.getModelObject();
						MapFolder parent = folder.getParent();
						folderDAO.delete(folder);

						goToFolderParentPage(parent);

					}


				}.setVisible(folder.getChildren().isEmpty()));
			}
		};

		add(folderView);


		ScaledMapFilter mapFilter = new ScaledMapFilter();
		mapFilter.owner().set(getUser());
		mapFilter.name().orderBy(true);
		decorateMapFilter(mapFilter);

		DataView<ScaledMap> mapView = new DataView<ScaledMap>("maps",
				FilterDataProvider.of(mapFilter, mapDAO)) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<ScaledMap> item) {
				ScaledMap map = item.getModelObject();

				item.add(new Label("name", map.getName()));
				item.add(new AbstractMapPreview("thumb", map, 128) {
					@Override
					protected void addOnDomReadyJavaScript(String canvasId, StringBuilder js, double factor) {

					}
				});
				item.add(new IconLink<ScaledMap>("view", item.getModel(),
						GlyphIcon.eyeOpen) {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						ScaledMap map = item.getModelObject();

						setResponsePage(new ViewMapPage(map));

					}
				});
				item.add(new IconLink<ScaledMap>("delete", item.getModel(),
						GlyphIcon.trash) {

					private static final long serialVersionUID = 1L;

					@Inject
					private AmazonS3Service amazon;

					@Override
					public void onClick() {
						ScaledMap map = item.getModelObject();

						ActionResult amazonResult;
						if (map.getAmazonKey() != null) {
							amazonResult = amazon.removeImage(map.getAmazonKey());
						} else {
							amazonResult = ActionResult.ok();
						}

						if (amazonResult.isOk()) {
							MapFolder folder = map.getFolder();
							mapDAO.delete(map);

							goToFolderParentPage(folder);
						} else {
							error(amazonResult.getMessage());
						}

					}
				}.setVisible(
						map.getFogOfWarShapes().isEmpty() && map.getGroups().isEmpty() && map.getTokens().isEmpty()));
			}

		};

		mapView.setItemsPerPage(10);
		add(mapView);
		add(new BootstrapPagingNavigator("mapnav", mapView));

	}

	protected abstract void decorateFolderFilter(@Nonnull MapFolderFilter folderFilter);

	protected abstract void decorateMapFilter(@Nonnull ScaledMapFilter mapFilter);

	private BeholderUser getUser() {
		return userModel.getObject();
	}

	private void goToFolderParentPage(MapFolder parent) {
		if (parent == null) {
			setResponsePage(new PrepareSessionPage());
		} else {
			setResponsePage(new ViewFolderPage(parent));
		}
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		userModel.detach();
	}
}
