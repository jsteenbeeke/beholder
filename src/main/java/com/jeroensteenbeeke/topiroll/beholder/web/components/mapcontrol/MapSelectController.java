/**
 * This file is part of Beholder
 * (C) 2016 Jeroen Steenbeeke
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jeroensteenbeeke.topiroll.beholder.web.components.mapcontrol;

import com.jeroensteenbeeke.hyperion.heinlein.web.components.AjaxIconLink;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.BootstrapPagingNavigator;
import com.jeroensteenbeeke.hyperion.icons.fontawesome.FontAwesome;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.MapFolderDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.ScaledMapDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapFolder;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.MapFolderFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.ScaledMapFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.components.AbstractMapPreview;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

public abstract class MapSelectController extends Panel {
	private static final long serialVersionUID = 1L;

	@Inject
	private ScaledMapDAO mapDAO;

	@Inject
	private MapFolderDAO folderDAO;

	@Inject
	private MapService mapService;

	private IModel<MapView> viewModel;

	private IModel<BeholderUser> userModel;

	public MapSelectController(String id, BeholderUser user, MapView view, @Nullable MapFolder folder) {
		super(id);
		setOutputMarkupId(true);

		this.viewModel = ModelMaker.wrap(view);
		this.userModel = ModelMaker.wrap(user);
		IModel<MapFolder> parentModel;
		if (folder != null && folder.getParent() != null) {
			parentModel = ModelMaker.wrap(folder.getParent());
		} else {
			parentModel = Model.of((MapFolder) null);
		}

		MapFolderFilter folderFilter = new MapFolderFilter();
		if (folder != null) {
			folderFilter.parent(folder);
		} else {
			folderFilter.parent().isNull();
		}
		folderFilter.name().orderBy(true);

		DataView<MapFolder> folderView =
				new DataView<MapFolder>("folders", FilterDataProvider.of(folderFilter, folderDAO)) {
					@Override
					protected void populateItem(Item<MapFolder> item) {
						MapFolder folder = item.getModelObject();

						AjaxLink<MapFolder> folderLink = new AjaxLink<MapFolder>("name", item.getModel()) {

							@Override
							public void onClick(AjaxRequestTarget target) {
								replaceMe(target,
										new MapSelectController(id, userModel.getObject(), viewModel.getObject(),
												getModelObject()) {
											@Override
											public void replaceMe(@Nonnull AjaxRequestTarget target,
																  @Nonnull WebMarkupContainer component) {
												MapSelectController.this.replaceMe(target, component);
											}

											@Override
											public void onMapSelected(@Nullable ScaledMap map,
																	  @Nonnull AjaxRequestTarget target) {
												MapSelectController.this.onMapSelected(map, target);
											}
										});
							}
						};

						folderLink.setBody(Model.of(folder.getName()));

						item.add(folderLink);
					}
				};
		add(folderView);

		ScaledMapFilter mapFilter = new ScaledMapFilter();
		mapFilter.name().orderBy(true);
		mapFilter.owner().set(user);

		if (folder != null) {
			mapFilter.folder(folder);
		} else {
			mapFilter.folder().isNull();
		}

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
				item.add(new AjaxIconLink<ScaledMap>("select", item.getModel(),
						FontAwesome.camera) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						mapService.selectMap(viewModel.getObject(),
								getModelObject());
						onMapSelected(getModelObject(), target);
					}
				});
			}

		};

		mapView.setItemsPerPage(10);
		add(mapView);
		add(new BootstrapPagingNavigator("mapnav", mapView));


		add(new AjaxIconLink<MapFolder>("back", parentModel, FontAwesome.chevron_left) {
			@Override
			public void onClick(AjaxRequestTarget target) {
				replaceMe(target,
						new MapSelectController(id, userModel.getObject(), viewModel.getObject(), getModelObject()) {
							@Override
							public void replaceMe(@Nonnull AjaxRequestTarget target,
												  @Nonnull WebMarkupContainer component) {
								MapSelectController.this.replaceMe(target, component);
							}

							@Override
							public void onMapSelected(@Nullable ScaledMap map, @Nonnull AjaxRequestTarget target) {
								MapSelectController.this.onMapSelected(map, target);
							}
						});
			}
		}.setVisible(folder != null));

		add(new AjaxIconLink<MapView>("unselect", viewModel,
				FontAwesome.unlink) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				mapService.unselectMap(getModelObject());
				onMapSelected(null, target);
			}
		});
	}

	@Override
	protected void onDetach() {
		super.onDetach();

		viewModel.detach();
		userModel.detach();
	}

	public abstract void replaceMe(@Nonnull AjaxRequestTarget target, @Nonnull WebMarkupContainer component);

	public abstract void onMapSelected(@Nullable ScaledMap map, @Nonnull AjaxRequestTarget target);
}
