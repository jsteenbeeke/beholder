package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;

import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.hyperion.util.TypedActionResult;
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
		
		add(new Link<ScaledMap>("back", ModelMaker.wrap(group.getMap())) {
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
				item.add(new NonCachingImage("thumb",
						shape.createThumbnailResource(200)));
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

					if (checked != null && checked.booleanValue()) {
						

						keep.add(shape);
					} else if (shape.getGroup() != null){
						remove.add(shape);
					}

				});

				TypedActionResult<FogOfWarGroup> result = mapService
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
