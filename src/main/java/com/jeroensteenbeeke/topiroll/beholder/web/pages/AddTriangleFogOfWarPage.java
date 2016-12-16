package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import java.awt.Dimension;
import java.awt.Graphics2D;

import javax.inject.Inject;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.time.Time;

import com.google.common.collect.Lists;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.form.dropdown.AjaxDropDownChoice;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableAdapter;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableBehavior;
import com.googlecode.wicket.jquery.ui.interaction.resizable.ResizableAdapter;
import com.googlecode.wicket.jquery.ui.interaction.resizable.ResizableBehavior;
import com.jeroensteenbeeke.hyperion.ducktape.web.renderer.LambdaRenderer;
import com.jeroensteenbeeke.hyperion.heinlein.web.resources.TouchPunchJavaScriptReference;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.hyperion.util.ImageUtil;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.TriangleOrientation;
import com.jeroensteenbeeke.topiroll.beholder.web.components.ImageContainer;
import com.jeroensteenbeeke.topiroll.beholder.web.components.MapEditSubmitPanel;
import com.jeroensteenbeeke.topiroll.beholder.web.resources.AbstractFogOfWarPreviewResource;

public class AddTriangleFogOfWarPage extends AuthenticatedPage {
	private static final long serialVersionUID = 1L;

	private NumberTextField<Integer> sidesField;

	private NumberTextField<Integer> offsetXField;

	private NumberTextField<Integer> offsetYField;

	private IModel<ScaledMap> mapModel;

	private DropDownChoice<TriangleOrientation> orientationSelect;

	public AddTriangleFogOfWarPage(ScaledMap map) {
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

		sidesField = new NumberTextField<>("sides", Model.of(imageWidth / 4));
		sidesField.setOutputMarkupId(true);
		sidesField.setMinimum(1);
		sidesField.setMaximum(imageWidth);
		sidesField.setRequired(true);
		sidesField.setEnabled(false);


		offsetXField = new NumberTextField<>("offsetX",
				Model.of(imageWidth / 2));
		offsetXField.setOutputMarkupId(true);
		offsetXField.setMinimum(0);
		offsetXField.setMaximum(imageWidth);
		offsetXField.setRequired(true);
		offsetXField.setEnabled(false);

		offsetYField = new NumberTextField<>("offsetY",
				Model.of(imageHeight / 2));
		offsetYField.setOutputMarkupId(true);
		offsetYField.setMinimum(0);
		offsetYField.setMaximum(imageHeight);
		offsetYField.setRequired(true);
		offsetYField.setEnabled(false);
		
		WebMarkupContainer areaMarker = new WebMarkupContainer("areaMarker");

		orientationSelect = new AjaxDropDownChoice<TriangleOrientation>(
				"orientation", Model.of(TriangleOrientation.TopLeft),
				Lists.newArrayList(TriangleOrientation.values()),
				LambdaRenderer.of(TriangleOrientation::getDescription)) {
					private static final long serialVersionUID = 1L;
					
					@Override
					public void onSelectionChanged(AjaxRequestTarget target) {
						target.add(areaMarker);
					}
			
		};
		orientationSelect.setRequired(true);
		
		

		final ImageContainer previewImage = new ImageContainer("preview",
				new ResourceReference(
						String.format("preview-%d", map.getId())) {
					private static final long serialVersionUID = 1L;

					@Override
					public IResource getResource() {
						AbstractFogOfWarPreviewResource resource = new AbstractFogOfWarPreviewResource(
								mapModel) {

							private static final long serialVersionUID = 1L;

							@Override
							public void drawShape(Graphics2D graphics2d) {
								setLastModifiedTime(Time.now());
							}
						};

						return resource;
					}

				}, dimensions);
		previewImage.setOutputMarkupId(true);

		
		areaMarker.add(AttributeModifier.replace("style",
				new LoadableDetachableModel<String>() {
					private static final long serialVersionUID = 1L;

					@Override
					protected String load() {
						StringBuilder builder = new StringBuilder();
						
						builder.append("left: ")
								.append(offsetXField.getModelObject())
								.append("px; ");
						builder.append("top: ")
								.append(offsetYField.getModelObject())
								.append("px; ");
						builder.append("width: 0px; ");
						builder.append("height: 0px; ");

						orientationSelect.getModelObject().renderCSS(builder,
								sidesField.getModelObject());

						return builder.toString();
					}
				}

		));

		Options draggableOptions = new Options();
		draggableOptions.set("opacity", "0.5");
		draggableOptions.set("containment", Options.asString("parent"));
		areaMarker.add(
				new DraggableBehavior(draggableOptions, new DraggableAdapter() {
					private static final long serialVersionUID = 1L;

					@Override
					public boolean isStopEventEnabled() {

						return true;
					}

					@Override
					public void onDragStop(AjaxRequestTarget target, int top,
							int left) {
						super.onDragStop(target, top, left);

						offsetXField.setModelObject(left);
						offsetYField.setModelObject(top);

						target.add(offsetXField, offsetYField);
					}
				}));
		Options resizableOptions = new Options();
		resizableOptions.set("containment", Options.asString("parent"));
		resizableOptions.set("handles", Options.asString("se"));
		resizableOptions.set("aspectRatio", "1.0");
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

						offsetXField.setModelObject(left);
						offsetYField.setModelObject(top);
						sidesField.setModelObject(width);

						target.add(offsetXField, offsetYField, sidesField,
								areaMarker);

					}

				}));
		previewImage.add(areaMarker);

		Form<ScaledMap> configureForm = new Form<ScaledMap>("configureForm",
				mapModel) {
			private static final long serialVersionUID = 1L;

			@Inject
			private MapService mapService;

			@Override
			protected void onSubmit() {
				ScaledMap map = mapModel.getObject();
				mapService.addFogOfWarTriangle(map, sidesField.getModelObject(),
						offsetXField.getModelObject(),
						offsetYField.getModelObject(), orientationSelect.getModelObject());
			}
		};

		configureForm.add(sidesField, orientationSelect);
		configureForm.add(offsetXField);
		configureForm.add(offsetYField);

		add(configureForm);

		add(previewImage);

		add(new MapEditSubmitPanel("submit", configureForm));
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);

		response.render(JavaScriptHeaderItem
				.forReference(TouchPunchJavaScriptReference.get()));
	}

	@Override
	protected void onDetach() {

		super.onDetach();

		mapModel.detach();
	}

	public NumberTextField<Integer> getWidthField() {
		return sidesField;
	}


	public NumberTextField<Integer> getOffsetXField() {
		return offsetXField;
	}

	public NumberTextField<Integer> getOffsetYField() {
		return offsetYField;
	}
}
