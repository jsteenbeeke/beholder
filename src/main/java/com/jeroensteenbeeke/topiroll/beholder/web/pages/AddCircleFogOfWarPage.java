package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import java.awt.Dimension;
import java.awt.Graphics2D;

import javax.inject.Inject;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.time.Time;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableAdapter;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableBehavior;
import com.googlecode.wicket.jquery.ui.interaction.resizable.ResizableAdapter;
import com.googlecode.wicket.jquery.ui.interaction.resizable.ResizableBehavior;
import com.jeroensteenbeeke.hyperion.heinlein.web.resources.TouchPunchJavaScriptReference;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.hyperion.util.ImageUtil;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.web.components.ImageContainer;
import com.jeroensteenbeeke.topiroll.beholder.web.components.MapEditSubmitPanel;
import com.jeroensteenbeeke.topiroll.beholder.web.resources.AbstractFogOfWarPreviewResource;

public class AddCircleFogOfWarPage extends AuthenticatedPage {
	private static final long serialVersionUID = 1L;

	private NumberTextField<Integer> radiusField;

	private NumberTextField<Integer> offsetXField;

	private NumberTextField<Integer> offsetYField;

	private IModel<ScaledMap> mapModel;

	public AddCircleFogOfWarPage(ScaledMap map) {
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

		radiusField = new NumberTextField<>("radius", Model.of(imageWidth / 16));
		radiusField.setMinimum(1);
		radiusField.setRequired(true);

		offsetXField = new NumberTextField<>("offsetX",
				Model.of(imageHeight / 2));
		offsetXField.setMinimum(0);
		offsetXField.setMaximum(imageWidth);

		offsetYField = new NumberTextField<>("offsetY",
				Model.of(imageHeight / 2));
		offsetYField.setMinimum(0);
		offsetYField.setMaximum(imageHeight);

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

		WebMarkupContainer areaMarker = new WebMarkupContainer("areaMarker");
		areaMarker.add(AttributeModifier.replace("style", String.format(
				"background-color: rgba(255, 0, 0, 0.5); border-radius: 100%%; width: %dpx; height: %dpx; left: %dpx; top: %dpx;",
				radiusField.getModelObject()*2, radiusField.getModelObject()*2,
				offsetXField.getModelObject(), offsetYField.getModelObject())));

		Options draggableOptions = new Options();
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

						int radius = (width + height) / 4;

						offsetXField.setModelObject(left);
						offsetYField.setModelObject(top);
						radiusField.setModelObject(radius);

						target.add(offsetXField, offsetYField, radiusField);

					}

				}));
		previewImage.add(areaMarker);

		Form<ScaledMap> configureForm = new Form<ScaledMap>("configureForm", mapModel) {
			private static final long serialVersionUID = 1L;

			@Inject
			private MapService mapService;

			@Override
			protected void onSubmit() {
				ScaledMap map = mapModel.getObject();
				mapService.addFogOfWarCircle(map, radiusField.getModelObject(),
						offsetXField.getModelObject(),
						offsetYField.getModelObject());
			}
		};

		configureForm.add(radiusField);
		configureForm.add(offsetXField);
		configureForm.add(offsetYField);

		add(configureForm);

		add(previewImage);

		add(new MapEditSubmitPanel("submit", configureForm));
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		
		response.render(JavaScriptHeaderItem.forReference(TouchPunchJavaScriptReference.get()));
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
