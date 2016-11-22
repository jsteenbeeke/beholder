package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;

public class AddRectFogOfWarPage extends AuthenticatedPage {

	private final class UpdatePreviewBehavior
			extends AjaxFormComponentUpdatingBehavior {
		private final Image previewImage;

		private static final long serialVersionUID = 1L;

		private UpdatePreviewBehavior(Image previewImage) {
			super("change");
			this.previewImage = previewImage;
		}

		@Override
		protected void onUpdate(AjaxRequestTarget target) {
			target.add(previewImage);

		}
	}

	private static final long serialVersionUID = 1L;

	private NumberTextField<Integer> widthField;
	
	private NumberTextField<Integer> heightField;

	private NumberTextField<Integer> offsetXField;

	private NumberTextField<Integer> offsetYField;

	private IModel<ScaledMap> mapModel;

	public AddRectFogOfWarPage(ScaledMap map, final byte[] image,
			final String originalName) {
		super("Configure map");

		this.mapModel = ModelMaker.wrap(map);

		add(new Link<BeholderUser>("back") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new ViewMapPage(mapModel.getObject()));

			}
		});

		widthField = new NumberTextField<>("width", Model.of(5));
		widthField.setMinimum(1);
		widthField.setRequired(true);
		
		heightField = new NumberTextField<>("height", Model.of(5));
		heightField.setMinimum(1);
		heightField.setRequired(true);

		offsetXField = new NumberTextField<>("offsetX", Model.of(0));
		offsetXField.setMinimum(0);

		offsetYField = new NumberTextField<>("offsetY", Model.of(0));
		offsetYField.setMinimum(0);

		final Image previewImage = new Image("preview",
				new FogOfWarRectPreviewResource(image,
						getRadiusField()::getModelObject,
						getOffsetXField()::getModelObject,
						getOffsetYField()::getModelObject));
		previewImage.setOutputMarkupId(true);

		widthField.add(new UpdatePreviewBehavior(previewImage));
		offsetXField.add(new UpdatePreviewBehavior(previewImage));
		offsetYField.add(new UpdatePreviewBehavior(previewImage));

		Form<ScaledMap> configureForm = new Form<ScaledMap>("configureForm") {
			private static final long serialVersionUID = 1L;

			@Inject
			private MapService mapService;

			@Override
			protected void onSubmit() {
				mapService.addFogOfWarRect(mapModel.getObject(), widthField.getModelObject(), heightField.getModelObject(), offsetXField.getModelObject(), offsetYField.getModelObject());
				setResponsePage(new OverviewPage());
			}
		};

		configureForm.add(widthField);
		configureForm.add(offsetXField);
		configureForm.add(offsetYField);

		add(configureForm);

		add(previewImage);

		add(new SubmitLink("submit", configureForm));
	}
	
	@Override
	protected void onDetach() {
	
		super.onDetach();
		
		mapModel.detach();
	}

	public NumberTextField<Integer> getRadiusField() {
		return widthField;
	}

	public NumberTextField<Integer> getOffsetXField() {
		return offsetXField;
	}

	public NumberTextField<Integer> getOffsetYField() {
		return offsetYField;
	}
}
