package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import java.awt.Dimension;

import javax.inject.Inject;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.DynamicImageResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableAdapter;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableBehavior;
import com.googlecode.wicket.jquery.ui.interaction.resizable.ResizableAdapter;
import com.googlecode.wicket.jquery.ui.interaction.resizable.ResizableBehavior;
import com.jeroensteenbeeke.hyperion.util.ImageUtil;
import com.jeroensteenbeeke.hyperion.util.Randomizer;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.web.components.ImageContainer;

public class UploadMapStep2Page extends AuthenticatedPage {

	private static final long serialVersionUID = 1L;

	private NumberTextField<Integer> squareSizeField;

	public UploadMapStep2Page(final byte[] image, final String originalName) {
		super("Configure map");

		add(new Link<BeholderUser>("back") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(OverviewPage.class);

			}
		});

		Dimension dimensions = ImageUtil.getImageDimensions(image);
		final int imageWidth = (int) dimensions.getWidth();
		final int imageHeight = (int) dimensions.getHeight();

		final TextField<String> nameField = new TextField<>("name",
				Model.of(originalName));
		nameField.setRequired(true);

		squareSizeField = new NumberTextField<>("squareSize", Model.of(5));
		squareSizeField.setOutputMarkupId(true);
		squareSizeField.setMinimum(1);
		squareSizeField.setMaximum(imageWidth);
		squareSizeField.setRequired(true);
		squareSizeField.setEnabled(false);

		final ImageContainer previewImage = new ImageContainer("preview",
				new ResourceReference(
						String.format("preview-%s", Randomizer.random(23))) {
					private static final long serialVersionUID = 1L;

					@Override
					public IResource getResource() {
						return new DynamicImageResource(
								ImageUtil.getWicketFormatType(image)) {

							private static final long serialVersionUID = 1L;

							@Override
							protected byte[] getImageData(
									Attributes attributes) {
								return ImageUtil.resize(image, imageWidth * 2,
										imageHeight * 2);
							}
						};
					}

				}, new Dimension(imageWidth*2, imageHeight * 2));
		previewImage.setOutputMarkupId(true);

		WebMarkupContainer areaMarker = new WebMarkupContainer("areaMarker");
		areaMarker.add(AttributeModifier.replace("style",
				String.format(
						"background-color: rgba(255, 0, 0, 0.5); width: %dpx; height: %dpx; left: %dpx; top: %dpx;",
						squareSizeField.getModelObject() * 2,
						squareSizeField.getModelObject() * 2, imageWidth,
						imageHeight)));

		Options draggableOptions = new Options();
		draggableOptions.set("opacity", "0.5");
		draggableOptions.set("containment", Options.asString("parent"));
		areaMarker.add(new DraggableBehavior(draggableOptions,
				new DraggableAdapter()));
		Options resizableOptions = new Options();
		resizableOptions.set("containment", Options.asString("parent"));
		resizableOptions.set("handles", Options.asString("se"));
		resizableOptions.set("aspectRatio", "true");
		areaMarker.add(new ResizableBehavior("#" + areaMarker.getMarkupId(),
				resizableOptions, new ResizableAdapter() {
					private static final long serialVersionUID = 1L;

					@Override
					public boolean isResizeStopEventEnabled() {
						return true;
					}

					@Override
					public void onResizeStop(AjaxRequestTarget target, int top,
							int left, int width, int height) {
						super.onResizeStop(target, top, left, width, height);

						squareSizeField.setModelObject(width / 2);

						target.add(squareSizeField);

					}

				}));
		previewImage.add(areaMarker);

		Form<ScaledMap> configureForm = new Form<ScaledMap>("configureForm") {
			private static final long serialVersionUID = 1L;

			@Inject
			private MapService mapService;

			@Override
			protected void onSubmit() {
				ScaledMap map = mapService.createMap(getUser(),
						nameField.getModelObject(),
						squareSizeField.getModelObject(), image);

				setResponsePage(new ViewMapPage(map));
			}
		};

		configureForm.add(nameField);
		configureForm.add(squareSizeField);

		add(configureForm);
		add(previewImage);

		add(new SubmitLink("submit", configureForm));
	}

	public NumberTextField<Integer> getSquareSizeField() {
		return squareSizeField;
	}

}
