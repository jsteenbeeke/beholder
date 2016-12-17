/**
 * This file is part of Beholder
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import java.awt.Dimension;

import javax.inject.Inject;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.DynamicImageResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.validation.validator.PatternValidator;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableAdapter;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableBehavior;
import com.googlecode.wicket.jquery.ui.interaction.resizable.ResizableAdapter;
import com.googlecode.wicket.jquery.ui.interaction.resizable.ResizableBehavior;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.BootstrapFeedbackPanel;
import com.jeroensteenbeeke.hyperion.heinlein.web.resources.TouchPunchJavaScriptReference;
import com.jeroensteenbeeke.hyperion.util.ImageUtil;
import com.jeroensteenbeeke.hyperion.util.Randomizer;
import com.jeroensteenbeeke.hyperion.util.TypedActionResult;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.web.components.ImageContainer;

public class UploadMapStep2Page extends AuthenticatedPage {

	private static final long serialVersionUID = 1L;

	private NumberTextField<Integer> squareSizeField;

	private NumberTextField<Integer> indicatorSizeField;

	public UploadMapStep2Page(final byte[] image, final String originalName) {
		super("Configure map");

		add(new Link<BeholderUser>("back") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(OverviewPage.class);

			}
		});
		
		add(new BootstrapFeedbackPanel("feedback"));

		Dimension dimensions = ImageUtil.getImageDimensions(image);
		final int imageWidth = (int) dimensions.getWidth();
		final int imageHeight = (int) dimensions.getHeight();

		final TextField<String> nameField = new TextField<>("name",
				Model.of(originalName));
		nameField.setRequired(true);
		nameField.add(new PatternValidator("[a-zA-Z0-9]+"));

		final Label indicatorLabel = new Label("indicator", new LoadableDetachableModel<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected String load() {
				return String.format("%d foot square", indicatorSizeField.getModelObject());
			}
		});
		indicatorLabel.setOutputMarkupId(true);
		add(indicatorLabel);
		
		squareSizeField = new NumberTextField<>("squareSize", Model.of(5));
		squareSizeField.setOutputMarkupId(true);
		squareSizeField.setMinimum(1);
		squareSizeField.setMaximum(imageWidth);
		squareSizeField.setRequired(true);
		squareSizeField.setEnabled(false);
		
		indicatorSizeField = new NumberTextField<>("indicatorSize", Model.of(5));
		indicatorSizeField.setOutputMarkupId(true);
		indicatorSizeField.setMinimum(5);
		indicatorSizeField.setStep(5);
		indicatorSizeField.setRequired(true);
		indicatorSizeField.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(indicatorLabel);
				
			}
		});

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
				int squareSizeOnMap = squareSizeField.getModelObject();
				int indicatorSize = indicatorSizeField.getModelObject();
				
				int squareSize = 5 * squareSizeOnMap / indicatorSize;
				
				TypedActionResult<ScaledMap> result = mapService.createMap(getUser(),
						nameField.getModelObject(),
						squareSize, image);
				if (result.isOk()) {
					setResponsePage(new ViewMapPage(result.getObject()));
				} else {
					error(result.getMessage());
				}
			}
		};

		configureForm.add(nameField);
		configureForm.add(squareSizeField, indicatorSizeField);

		add(configureForm);
		add(previewImage);

		add(new SubmitLink("submit", configureForm));
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		
		response.render(JavaScriptHeaderItem.forReference(TouchPunchJavaScriptReference.get()));
	}

	public NumberTextField<Integer> getSquareSizeField() {
		return squareSizeField;
	}

}
