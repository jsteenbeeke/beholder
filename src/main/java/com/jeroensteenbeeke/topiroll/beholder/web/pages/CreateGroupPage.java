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

public class CreateGroupPage extends AuthenticatedPage {
	private static final long serialVersionUID = 1L;

	@Inject
	private FogOfWarShapeDAO shapeDAO;

	public CreateGroupPage(ScaledMap map) {
		super("Create Group");
		
		add(new Link<ScaledMap>("back", ModelMaker.wrap(map)) {
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
				item.add(new NonCachingImage("thumb",
						shape.createThumbnailResource(200)));
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

					if (checked != null && checked.booleanValue()) {
						FogOfWarShape shape = i.getModelObject();

						shapes.add(shape);
					}

				});

				TypedActionResult<FogOfWarGroup> result = mapService
						.createGroup(getModelObject(),
								nameField.getModelObject(), shapes);
				if (result.isOk()) {
					setResponsePage(new ViewMapPage(getModelObject()));
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
