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
package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster;

import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Inject;

import com.jeroensteenbeeke.topiroll.beholder.web.components.AbstractMapPreview;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;

import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.lux.TypedResult;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarShapeDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarGroup;
import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarShape;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.FogOfWarShapeFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.components.MapEditSubmitPanel;

public class CreateGroupPage extends AuthenticatedPage {
	private static final long serialVersionUID = 1L;

	@Inject
	private FogOfWarShapeDAO shapeDAO;

	public CreateGroupPage(ScaledMap map) {
		super("Create Group");
		
		add(new Link<>("back", ModelMaker.wrap(map)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new ViewMapPage(getModelObject()));

			}
		});

		FogOfWarShapeFilter shapeFilter = new FogOfWarShapeFilter();
		shapeFilter.map().set(map);
		shapeFilter.group().isNull();

		DataView<FogOfWarShape> shapeView = new DataView<FogOfWarShape>(
				"shapes", FilterDataProvider.of(shapeFilter, shapeDAO)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<FogOfWarShape> item) {
				FogOfWarShape shape = item.getModelObject();

				item.add(new CheckBox("check", Model.of(false)));
				item.add(new AbstractMapPreview("thumb", shape.getMap(), 200) {
					private static final long serialVersionUID = 6355406078071376558L;

					@Override
					protected void addOnDomReadyJavaScript(String canvasId, StringBuilder js, double factor) {
						js.append(item.getModelObject().visit(new FogOfWarPreviewRenderer(canvasId, factor)));
					}
				});
				item.add(new Label("description", shape.getDescription()));

			}
		};

		TextField<String> nameField = new TextField<>("name", Model.of());
		nameField.setRequired(true);

		Form<ScaledMap> groupForm = new Form<ScaledMap>("form",
				ModelMaker.wrap(map)) {
			private static final long serialVersionUID = 1L;

			@Inject
			private MapService mapService;

			@Override
			protected void onSubmit() {
				super.onSubmit();

				List<FogOfWarShape> shapes = new ArrayList<>();

				shapeView.getItems().forEachRemaining(i -> {
					CheckBox box = (CheckBox) i.get("check");
					Boolean checked = box.getModelObject();

					if (checked != null && checked) {
						FogOfWarShape shape = i.getModelObject();

						shapes.add(shape);
					}

				});

				TypedResult<FogOfWarGroup> result = mapService
						.createGroup(getModelObject(),
								nameField.getModelObject(), shapes);
				if (!result.isOk()) {
					error(result.getMessage());
				}
			}

		};

		groupForm.add(shapeView);
		groupForm.add(nameField);
		groupForm.add(new MapEditSubmitPanel("submit", groupForm));
		add(groupForm);
	}
}
