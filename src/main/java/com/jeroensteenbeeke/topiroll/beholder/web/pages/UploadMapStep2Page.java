package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;

import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.web.resources.GridOverlayImageResource;

public class UploadMapStep2Page extends AuthenticatedPage {

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

	private NumberTextField<Integer> squareSizeField;

	private NumberTextField<Integer> offsetXField;

	private NumberTextField<Integer> offsetYField;

	public UploadMapStep2Page(final byte[] image, final String originalName) {
		super("Configure map");

		add(new Link<BeholderUser>("back") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new OverviewPage());

			}
		});

		final TextField<String> nameField = new TextField<>("name",
				Model.of(originalName));
		nameField.setRequired(true);

		squareSizeField = new NumberTextField<>("square", Model.of(5));
		squareSizeField.setMinimum(2);
		squareSizeField.setRequired(true);

		offsetXField = new NumberTextField<>("offsetX", Model.of(0));
		offsetXField.setMinimum(0);

		offsetYField = new NumberTextField<>("offsetY", Model.of(0));
		offsetYField.setMinimum(0);

		final Image previewImage = new NonCachingImage("preview",
				new GridOverlayImageResource(image,
						getSquareSizeField()::getModelObject,
						getOffsetXField()::getModelObject,
						getOffsetYField()::getModelObject));
		previewImage.setOutputMarkupId(true);

		squareSizeField.add(new UpdatePreviewBehavior(previewImage));
		offsetXField.add(new UpdatePreviewBehavior(previewImage));
		offsetYField.add(new UpdatePreviewBehavior(previewImage));

		Form<ScaledMap> configureForm = new Form<ScaledMap>("configureForm") {
			private static final long serialVersionUID = 1L;

			@Inject
			private MapService mapService;

			@Override
			protected void onSubmit() {

				mapService.createMap(getUser(), nameField.getModelObject(),
						squareSizeField.getModelObject(), image);

				setResponsePage(new OverviewPage());
			}
		};

		configureForm.add(squareSizeField);
		configureForm.add(offsetXField);
		configureForm.add(offsetYField);
		configureForm.add(nameField);

		add(configureForm);

		add(previewImage);

		add(new SubmitLink("submit", configureForm));
	}

	public NumberTextField<Integer> getSquareSizeField() {
		return squareSizeField;
	}

	public NumberTextField<Integer> getOffsetXField() {
		return offsetXField;
	}

	public NumberTextField<Integer> getOffsetYField() {
		return offsetYField;
	}
}
