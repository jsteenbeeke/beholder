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
import com.jeroensteenbeeke.topiroll.beholder.web.resources.FogOfWarCirclePreviewResource;

public class AddCircleFogOfWarPage extends AuthenticatedPage {

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

	private NumberTextField<Integer> radiusField;

	private NumberTextField<Integer> offsetXField;

	private NumberTextField<Integer> offsetYField;

	private IModel<ScaledMap> mapModel;

	public AddCircleFogOfWarPage(ScaledMap map, final byte[] image,
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

		radiusField = new NumberTextField<>("radius", Model.of(5));
		radiusField.setMinimum(1);
		radiusField.setRequired(true);

		offsetXField = new NumberTextField<>("offsetX", Model.of(0));
		offsetXField.setMinimum(0);

		offsetYField = new NumberTextField<>("offsetY", Model.of(0));
		offsetYField.setMinimum(0);

		final Image previewImage = new Image("preview",
				new FogOfWarCirclePreviewResource(image,
						getRadiusField()::getModelObject,
						getOffsetXField()::getModelObject,
						getOffsetYField()::getModelObject));
		previewImage.setOutputMarkupId(true);

		radiusField.add(new UpdatePreviewBehavior(previewImage));
		offsetXField.add(new UpdatePreviewBehavior(previewImage));
		offsetYField.add(new UpdatePreviewBehavior(previewImage));

		Form<ScaledMap> configureForm = new Form<ScaledMap>("configureForm") {
			private static final long serialVersionUID = 1L;

			@Inject
			private MapService mapService;

			@Override
			protected void onSubmit() {
				mapService.addFogOfWarCircle(mapModel.getObject(), radiusField.getModelObject(), offsetXField.getModelObject(), offsetYField.getModelObject());
				setResponsePage(new OverviewPage());
			}
		};

		configureForm.add(radiusField);
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
		return radiusField;
	}

	public NumberTextField<Integer> getOffsetXField() {
		return offsetXField;
	}

	public NumberTextField<Integer> getOffsetYField() {
		return offsetYField;
	}
}