/*
 * This file is part of Beholder
 * Copyright (C) 2016 - 2023 Jeroen Steenbeeke
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
package com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.exploration;

import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.PortraitDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.PortraitVisibilityDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.Portrait;
import com.jeroensteenbeeke.topiroll.beholder.entities.PortraitVisibilityLocation;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.PortraitFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.PortraitVisibilityFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMModalWindow;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewCallback;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ExternalImage;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

import jakarta.inject.Inject;
import java.util.List;

public class PortraitsWindow extends DMModalWindow<MapView> {
	private static final long serialVersionUID = -2255734962780199594L;
	@Inject
	private PortraitDAO portraitDAO;

	@Inject
	private MapService mapService;

	public PortraitsWindow(String id, MapView view, DMViewCallback callback) {
		super(id, ModelMaker.wrap(view), "Portraits");

		PortraitFilter portraitFilter = new PortraitFilter();
		portraitFilter.owner(view.getOwner()).name().orderBy(true);
		view.getOwner().activeCampaign().peek(c -> portraitFilter.campaign().isNull().orCampaign(c));

		WebMarkupContainer container = new WebMarkupContainer("container");
		container.setOutputMarkupId(true);

		DataView<Portrait> portraitView = new DataView<Portrait>("portraits",
			FilterDataProvider.of(portraitFilter, portraitDAO)) {
			private static final long serialVersionUID = 3427970936660497988L;

			@Override
			protected void populateItem(Item<Portrait> item) {
				Portrait portrait = item.getModelObject();
				item.add(new Label("name", portrait.getName()));
				item.add(new ExternalImage("thumb",
					portrait.getImageUrl()));
				item.add(new ListView<>("locations", List.of(PortraitVisibilityLocation.values())) {

					private static final long serialVersionUID = 152264260845309393L;
					@Inject
					private PortraitVisibilityDAO visibilityDAO;

					@Override
					protected void populateItem(ListItem<PortraitVisibilityLocation> innerItem) {
						PortraitVisibilityLocation location = innerItem.getModelObject();

						final boolean selected = visibilityDAO.findByFilter(
								new PortraitVisibilityFilter().view(PortraitsWindow.this.getModelObject())
									.portrait(item.getModelObject()))
							.find(v -> v.getLocation().equals(location)).isDefined();

						AjaxLink<PortraitVisibilityLocation> link = new AjaxLink<PortraitVisibilityLocation>(
							"button") {

							private static final long serialVersionUID = -7633978000647082838L;

							@Override
							public void onClick(AjaxRequestTarget target) {
								if (selected) {
									mapService.unselectPortrait(PortraitsWindow.this.getModelObject(),
										item.getModelObject(), location);
								} else {
									mapService.selectPortrait(PortraitsWindow.this.getModelObject(),
										item.getModelObject(), location);
								}

								target.add(container);
							}
						};

						link.add(AttributeModifier.replace("class", new LoadableDetachableModel<String>() {
							private static final long serialVersionUID = -317551053297479895L;

							@Override
							protected String load() {
								if (!selected) {
									return "btn btn-default";
								}
								return "btn btn-primary";
							}
						}));

						link.setBody(Model.of(location.getDisplayValue()));

						innerItem.add(link);
					}
				});
			}
		};
		portraitView.setOutputMarkupId(true);
		container.add(portraitView);
		add(container);

		getBody().add(AttributeModifier.replace("style", "height: 300px; overflow: auto;"));
	}
}
