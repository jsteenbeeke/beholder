package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import java.awt.Dimension;

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
import com.jeroensteenbeeke.hyperion.util.ImageUtil;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.web.resources.FogOfWarRectPreviewResource;

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

	public AddRectFogOfWarPage(ScaledMap map) {
		super("Configure map");

		this.mapModel = ModelMaker.wrap(map);

		add(new Link<BeholderUser>("back") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new ViewMapPage(mapModel.getObject()));

			}
		});
		
		Dimension dimensions = ImageUtil.getImageDimensions(map.getData());
		final int imageWidth = (int) dimensions.getWidth();
		final int imageHeight = (int) dimensions.getHeight();
		
		widthField = new NumberTextField<>("width", Model.of(imageWidth / 4));
		widthField.setMinimum(1);
		widthField.setMaximum(imageWidth);
		widthField.setRequired(true);
		
		heightField = new NumberTextField<>("height", Model.of(imageHeight / 4));
		heightField.setMinimum(1);
		heightField.setMaximum(imageHeight);
		heightField.setRequired(true);

		offsetXField = new NumberTextField<>("offsetX", Model.of(imageWidth / 2));
		offsetXField.setMinimum(0);
		offsetXField.setMaximum(imageWidth);

		offsetYField = new NumberTextField<>("offsetY", Model.of(imageHeight / 2));
		offsetYField.setMinimum(0);
		offsetYField.setMaximum(imageHeight);

		final Image previewImage = new Image("preview",
				new FogOfWarRectPreviewResource(mapModel,
						getWidthField()::getModelObject,
						getHeightField()::getModelObject,
						getOffsetXField()::getModelObject,
						getOffsetYField()::getModelObject));
		previewImage.setOutputMarkupId(true);

		heightField.add(new UpdatePreviewBehavior(previewImage));
		widthField.add(new UpdatePreviewBehavior(previewImage));
		offsetXField.add(new UpdatePreviewBehavior(previewImage));
		offsetYField.add(new UpdatePreviewBehavior(previewImage));

		Form<ScaledMap> configureForm = new Form<ScaledMap>("configureForm") {
			private static final long serialVersionUID = 1L;

			@Inject
			private MapService mapService;

			@Override
			protected void onSubmit() {
				ScaledMap map = mapModel.getObject();
				mapService.addFogOfWarRect(map, widthField.getModelObject(), heightField.getModelObject(), offsetXField.getModelObject(), offsetYField.getModelObject());
				setResponsePage(new ViewMapPage(map));
			}
		};

		configureForm.add(widthField, heightField);
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

	public NumberTextField<Integer> getWidthField() {
		return widthField;
	}
	
	public NumberTextField<Integer> getHeightField() {
		return heightField;
	}

	public NumberTextField<Integer> getOffsetXField() {
		return offsetXField;
	}

	public NumberTextField<Integer> getOffsetYField() {
		return offsetYField;
	}
}
