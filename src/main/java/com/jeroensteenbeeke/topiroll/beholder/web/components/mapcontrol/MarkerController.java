/**
 * This file is part of Beholder
 * (C) 2016 Jeroen Steenbeeke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.jeroensteenbeeke.topiroll.beholder.web.components.mapcontrol;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;

import com.jeroensteenbeeke.hyperion.heinlein.web.components.AjaxIconLink;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.GlyphIcon;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.topiroll.beholder.dao.AreaMarkerDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.AreaMarker;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.AreaMarkerFilter;

public abstract class MarkerController extends Panel {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private AreaMarkerDAO markerDAO;
	
	public MarkerController(String id, MapView view) {
		super(id);
		
		AreaMarkerFilter filter = new AreaMarkerFilter();
		filter.view().set(view);
		
		add(new DataView<AreaMarker>("markers", FilterDataProvider.of(filter, markerDAO)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<AreaMarker> item) {
				AreaMarker marker = item.getModelObject();
				
				item.add(marker.createPanel("marker"));
				item.add(new AjaxIconLink<AreaMarker>("delete", item.getModel(), GlyphIcon.trash) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						AreaMarker marker = item.getModelObject();
						markerDAO.delete(marker);
						replaceMe(target);
					}
				});
			}
		});
	}

	public abstract void replaceMe(AjaxRequestTarget target);
}
