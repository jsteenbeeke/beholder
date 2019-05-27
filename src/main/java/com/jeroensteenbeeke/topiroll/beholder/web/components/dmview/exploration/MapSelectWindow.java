package com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.exploration;

import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.dao.MapFolderDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.ScaledMapDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.MapFolderFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.ScaledMapFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMModalWindow;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewCallback;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MapSelectWindow extends DMModalWindow<MapView> {
	private static final long serialVersionUID = -4375530900574500868L;
	@Inject
	private MapFolderDAO mapFolderDAO;

	@Inject
	private ScaledMapDAO mapDAO;

	public MapSelectWindow(String id, MapView view, DMViewCallback callback) {
		super(id, ModelMaker.wrap(view), "Select Map");

		ScaledMapFilter rootFilter = new ScaledMapFilter();
		rootFilter.owner(view.getOwner());
		rootFilter.folder().isNull();
		rootFilter.name().orderBy(true);

		add(new MapDataView("rootmaps", view, rootFilter, mapDAO));

		List<MapFolder> sortedFolders = determineFolders(view.getOwner());

		add(new ListView<>("folders", ModelMaker.wrapList(sortedFolders)) {

			private static final long serialVersionUID = 2840801737582014664L;

			@Override
			protected void populateItem(ListItem<MapFolder> item) {
				MapFolder folder = item.getModelObject();

				item.add(new Label("foldername", folder.getNameWithParents()));

				ScaledMapFilter folderFilter = new ScaledMapFilter();
				folderFilter.folder(folder);
				folderFilter.name().orderBy(true);

				item.add(new MapDataView("maps", MapSelectWindow.this.getModelObject(), folderFilter,
					mapDAO));
			}
		});


		getBody().add(AttributeModifier.replace("style", "height: 300px; overflow: auto;"));
	}

	private List<MapFolder> determineFolders(BeholderUser owner) {
		List<MapFolder> result = new ArrayList<>();


		MapFolderFilter mapFolderFilter = new MapFolderFilter();
		mapFolderFilter.parent().isNull();
		mapFolderFilter.name().orderBy(true);

		for (MapFolder folder : mapFolderDAO.findByFilter(mapFolderFilter)) {
			result.addAll(currentAndSortedChildren(folder));
		}


		return result;
	}

	private List<MapFolder> currentAndSortedChildren(MapFolder folder) {
		List<MapFolder> result = new ArrayList<>();

		result.add(folder);

		for (MapFolder child : folder.getChildren().stream().sorted(Comparator.comparing(MapFolder::getName)).collect(Collectors.toList())) {
			result.addAll(currentAndSortedChildren(child));
		}

		return result;
	}

	private class MapDataView extends DataView<ScaledMap> {
		private static final long serialVersionUID = 1844667596896296793L;
		private final IModel<MapView> viewModel;

		private MapDataView(String id, MapView view, ScaledMapFilter filter, ScaledMapDAO mapDAO) {
			super(id, FilterDataProvider.of(filter, mapDAO));
			this.viewModel = ModelMaker.wrap(view);
		}

		@Override
		protected void populateItem(Item<ScaledMap> item) {
			ScaledMap map = item.getModelObject();

			PageParameters params = new PageParameters();
			params.set("map", map.getId());
			params.set("view", MapSelectWindow.this.getModelObject().getId());

			BookmarkablePageLink<MapLink> link = new BookmarkablePageLink<>("select",
					ExplorationModeMapSwitchHandlerPage.class, params);

			link.setBody(Model.of(map.getName()));

			item.add(link);


		}

		@Override
		protected void onDetach() {
			super.onDetach();

			viewModel.detach();
		}
	}
}
