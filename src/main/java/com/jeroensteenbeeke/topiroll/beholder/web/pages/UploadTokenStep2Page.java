package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import javax.inject.Inject;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.resource.DynamicImageResource;

import com.google.common.collect.Lists;
import com.jeroensteenbeeke.hyperion.ducktape.web.renderer.LambdaRenderer;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenSize;

public class UploadTokenStep2Page extends AuthenticatedPage {

	private static final long serialVersionUID = 1L;

	public UploadTokenStep2Page(final byte[] image, final String originalName) {
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

		final DropDownChoice<TokenSize> sizeSelect = new DropDownChoice<>(
				"size", Model.of(TokenSize.MEDIUM),
				new ListModel<TokenSize>(
						Lists.newArrayList(TokenSize.values())),
				LambdaRenderer.forEnum(TokenSize.class, TokenSize::name));
		sizeSelect.setRequired(true);

		final Image previewImage = new NonCachingImage("preview",
				new DynamicImageResource() {
					private static final long serialVersionUID = 1L;

					@Override
					protected byte[] getImageData(Attributes attributes) {
						return image;
					}
				});
		previewImage.setOutputMarkupId(true);

		Form<ScaledMap> configureForm = new Form<ScaledMap>("configureForm") {
			private static final long serialVersionUID = 1L;

			@Inject
			private MapService mapService;

			@Override
			protected void onSubmit() {

				mapService.createToken(getUser(), nameField.getModelObject(),
						sizeSelect.getModelObject(), image);

				setResponsePage(new OverviewPage());
			}
		};

		configureForm.add(sizeSelect);
		configureForm.add(nameField);

		add(configureForm);

		add(previewImage);

		add(new SubmitLink("submit", configureForm));
	}
}
