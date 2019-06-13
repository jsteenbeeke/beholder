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

import com.jeroensteenbeeke.hyperion.solstice.data.IByFunctionModel;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeParticipant;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewCallback;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewPanel;
import com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.CreateTokenWindow;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.LoadableDetachableModel;

import javax.inject.Inject;
import java.awt.*;
import java.util.Optional;

public class MapOptionsPanel extends DMViewPanel<MapView> {
	private static final long serialVersionUID = 8357448484143991994L;

	public MapOptionsPanel(String id, MapView view, DMViewCallback callback) {
		super(id);

		IByFunctionModel<MapView> viewModel = ModelMaker.wrap(view);
		setModel(viewModel);

		add(new Label("location", new LoadableDetachableModel<String>() {
			private static final long serialVersionUID = 997699127776378442L;

			@Override
			protected String load() {
				return callback.getClickedLocation().map(p -> String.format
					("(%d, %d)", p.x, p.y)).orElse("-");
			}
		}));

		add(new AjaxLink<InitiativeParticipant>("gather") {
			private static final long serialVersionUID = 1272429848206149308L;
			@Inject
			private MapService mapService;

			@Override
			public void onClick(AjaxRequestTarget target) {
				MapView view = MapOptionsPanel.this.getModelObject();

				callback.getClickedLocation().ifPresent(p -> {

					mapService.gatherPlayerTokens(view, p.x, p.y);

					callback.redrawMap(target);
				});
			}
		});

		add(new AjaxLink<MapView>("newcirclemarker") {
			private static final long serialVersionUID = -1424328546866932837L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				callback.createModalWindow(target, CreateCircleMarkerWindow::new, MapOptionsPanel.this.getModelObject());
			}
		});

		add(new AjaxLink<InitiativeParticipant>("newcubemarker") {
			private static final long serialVersionUID = -5454136713960217622L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				callback.createModalWindow(target, CreateCubeMarkerWindow::new, MapOptionsPanel.this.getModelObject());

			}

		});

		add(new AjaxLink<InitiativeParticipant>("newraymarker") {
			private static final long serialVersionUID = 4114140288206643781L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				callback.createModalWindow(target, CreateLineMarkerWindow::new, MapOptionsPanel.this.getModelObject());

			}

			@Override
			public boolean isVisible() {
				return super.isVisible() && callback.getPreviousClickedLocation().isPresent();
			}
		});

		add(new AjaxLink<InitiativeParticipant>("newconemarker") {
			private static final long serialVersionUID = -4624559515716244012L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				callback.createModalWindow(target, CreateConeMarkerWindow::new, MapOptionsPanel.this.getModelObject());
			}

			@Override
			public boolean isVisible() {
				return super.isVisible() && callback.getPreviousClickedLocation().isPresent();
			}

		});

		add(new AjaxLink<ScaledMap>("newtoken", viewModel.getProperty(MapView::getSelectedMap)) {
			private static final long serialVersionUID = 2836526275140755418L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				ScaledMap map = getModelObject();
				if (map != null) {
					callback.createModalWindow(target, CreateTokenWindow::new, map);
				}
			}
		});


	}
}
