/**
 * This file is part of Beholder
 * (C) 2016-2019 Jeroen Steenbeeke
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
package com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.combat;

import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.AreaMarkerDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewCallback;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;

import javax.inject.Inject;

public class MarkerStatusPanel extends DMViewPanel<MapView> {
	private static final long serialVersionUID = 2812527034678281465L;

	@Inject
	private MapService mapService;

	public MarkerStatusPanel(String id, DMViewCallback callback) {
		super(id);

		add(new AjaxLink<TokenInstance>("remove") {
			private static final long serialVersionUID = 7679288508672302142L;

			@Inject
			private AreaMarkerDAO markerDAO;

			@Override
			public void onClick(AjaxRequestTarget target) {
				markerDAO.delete(callback.getSelectedMarker());

				callback.redrawMap(target);

				MarkerStatusPanel.this.setVisible(false);
				target.add(MarkerStatusPanel.this);
			}
		});

	}

}
