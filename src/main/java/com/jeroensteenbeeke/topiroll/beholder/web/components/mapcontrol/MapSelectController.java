/**
 * This file is part of Beholder
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jeroensteenbeeke.topiroll.beholder.web.components.mapcontrol;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;

import com.jeroensteenbeeke.hyperion.ducktape.web.resources.ThumbnailResource;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.AjaxIconLink;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.BootstrapPagingNavigator;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.GlyphIcon;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.ScaledMapDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.ScaledMapFilter;

public class MapSelectController extends Panel {
	private static final long serialVersionUID = 1L;

	@Inject
	private ScaledMapDAO mapDAO;

	@Inject
	private MapService mapService;
	
	private IModel<MapView> viewModel;

	public MapSelectController(String id, BeholderUser user, MapView view) {
		super(id);
		setOutputMarkupId(true);
		
		this.viewModel = ModelMaker.wrap(view);

		ScaledMapFilter mapFilter = new ScaledMapFilter();
		mapFilter.name().orderBy(true);
		mapFilter.owner().set(user);

		DataView<ScaledMap> mapView = new DataView<ScaledMap>("maps",
				FilterDataProvider.of(mapFilter, mapDAO)) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<ScaledMap> item) {
				ScaledMap map = item.getModelObject();

				item.add(new Label("name", map.getName()));
				item.add(new NonCachingImage("thumb",
						new ThumbnailResource(128, map.getData())));
				item.add(new AjaxIconLink<ScaledMap>("select", item.getModel(),
						GlyphIcon.screenshot) {
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

		add(new AjaxIconLink<MapView>("unselect", viewModel,
				GlyphIcon.removeCircle) {
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
	}
	
	public void onMapSelected(@Nullable ScaledMap map, @Nonnull AjaxRequestTarget target) {
		
	}
}
