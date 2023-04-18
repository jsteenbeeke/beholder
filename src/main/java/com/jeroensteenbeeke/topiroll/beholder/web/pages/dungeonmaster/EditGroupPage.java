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

import javax.inject.Inject;

import com.jeroensteenbeeke.topiroll.beholder.web.components.AbstractMapPreview;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
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

public class EditGroupPage extends AuthenticatedPage {
	private static final long serialVersionUID = 1L;

	@Inject
	private FogOfWarShapeDAO shapeDAO;

	public EditGroupPage(FogOfWarGroup group) {
		super("Edit Group");

		add(new Link<>("back", ModelMaker.wrap(group.getMap())) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new ViewMapPage(getModelObject()));

			}
		});

		FogOfWarShapeFilter shapeFilter = new FogOfWarShapeFilter();
		shapeFilter.map().set(group.getMap());
		shapeFilter.group().equalToOrNull(group);

		DataView<FogOfWarShape> shapeView = new DataView<FogOfWarShape>(
				"shapes", FilterDataProvider.of(shapeFilter, shapeDAO)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<FogOfWarShape> item) {
				FogOfWarShape shape = item.getModelObject();

				item.add(new CheckBox("check", Model.of(shape.getGroup() != null)));
				item.add(new AbstractMapPreview("thumb", shape.getMap(), 200) {
					private static final long serialVersionUID = -4705313092728699291L;

					@Override
					protected void addOnDomReadyJavaScript(String canvasId, StringBuilder js, double factor) {
						js.append(item.getModelObject().visit(new FogOfWarPreviewRenderer(canvasId, factor)));
					}
				});
				item.add(new Label("description", shape.getDescription()));

			}
		};

		TextField<String> nameField = new TextField<>("name", Model.of(group.getName()));
		nameField.setRequired(true);

		Form<FogOfWarGroup> groupForm = new Form<FogOfWarGroup>("form",
				ModelMaker.wrap(group)) {
			private static final long serialVersionUID = 1L;

			@Inject
			private MapService mapService;

			@Override
			protected void onSubmit() {
				super.onSubmit();

				List<FogOfWarShape> keep = new ArrayList<>();
				List<FogOfWarShape> remove = new ArrayList<>();

				shapeView.getItems().forEachRemaining(i -> {
					CheckBox box = (CheckBox) i.get("check");
					Boolean checked = box.getModelObject();

					FogOfWarShape shape = i.getModelObject();

					if (checked != null && checked) {


						keep.add(shape);
					} else if (shape.getGroup() != null) {
						remove.add(shape);
					}

				});

				TypedResult<FogOfWarGroup> result = mapService
						.editGroup(getModelObject(),
								nameField.getModelObject(), keep, remove);
				if (result.isOk()) {
					setResponsePage(new ViewMapPage(getModelObject().getMap()));
				} else {
					error(result.getMessage());
				}
			}

		};

		groupForm.add(shapeView);
		groupForm.add(nameField);
		groupForm.add(new SubmitLink("submit"));
		add(groupForm);
	}
}
