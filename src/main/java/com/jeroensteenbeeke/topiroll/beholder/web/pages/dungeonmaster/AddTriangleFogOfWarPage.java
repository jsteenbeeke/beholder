/**
 * This file is part of Beholder
 * (C) 2016 Jeroen Steenbeeke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster;

import com.google.common.collect.Lists;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableAdapter;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableBehavior;
import com.googlecode.wicket.jquery.ui.interaction.resizable.ResizableAdapter;
import com.googlecode.wicket.jquery.ui.interaction.resizable.ResizableBehavior;
import com.jeroensteenbeeke.hyperion.ducktape.web.renderer.LambdaRenderer;
import com.jeroensteenbeeke.hyperion.heinlein.web.resources.TouchPunchJavaScriptReference;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.hyperion.util.ImageUtil;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.entities.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.visitors.FogOfWarShapeVisitor;
import com.jeroensteenbeeke.topiroll.beholder.web.components.AbstractMapPreviewPanel;
import com.jeroensteenbeeke.topiroll.beholder.web.components.ImageContainer;
import com.jeroensteenbeeke.topiroll.beholder.web.components.MapEditSubmitPanel;
import com.jeroensteenbeeke.topiroll.beholder.web.resources.AbstractFogOfWarPreviewResource;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
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

import javax.inject.Inject;
import java.awt.*;

public class AddTriangleFogOfWarPage extends AuthenticatedPage {
	private static final long serialVersionUID = 1L;

	private NumberTextField<Integer> widthField;

	private NumberTextField<Integer> heightField;


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

		widthField = new NumberTextField<>("width", Model.of(imageWidth / 4));
		widthField.setOutputMarkupId(true);
		widthField.setMinimum(1);
		widthField.setMaximum(imageWidth);
		widthField.setRequired(true);
		widthField.setEnabled(false);

		heightField = new NumberTextField<>("height",
				Model.of(imageHeight / 4));
		heightField.setOutputMarkupId(true);
		heightField.setMinimum(1);
		heightField.setMaximum(imageHeight);
		heightField.setRequired(true);
		heightField.setEnabled(false);


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

		orientationSelect = new DropDownChoice<TriangleOrientation>(
				"orientation", Model.of(TriangleOrientation.TopLeft),
				Lists.newArrayList(TriangleOrientation.values()),
				LambdaRenderer.of(TriangleOrientation::getDescription));
		
		orientationSelect.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			
				target.add(areaMarker);
				
			}
		});
				
		orientationSelect.setRequired(true);
		
		

		final AbstractMapPreviewPanel previewImage = new AbstractMapPreviewPanel("preview", map) {
			@Override
			protected void addOnDomReadyJavaScript(StringBuilder js) {
				getModelObject().getAllShapes().forEach(s -> s.visit(new FogOfWarShapeVisitor<Void>() {
					@Override
					public Void visit(FogOfWarCircle fogOfWarCircle) {

						return null;
					}

					@Override
					public Void visit(FogOfWarRect fogOfWarRect) {
						return null;
					}

					@Override
					public Void visit(FogOfWarTriangle fogOfWarTriangle) {
						return null;
					}
				}));
			}
		};
		previewImage.setOutputMarkupId(true);

		
		areaMarker.add(AttributeModifier.replace("style",
				new LoadableDetachableModel<String>() {
					private static final long serialVersionUID = 1L;

					@Override
					protected String load() {
						StringBuilder builder = new StringBuilder();
						
						builder.append("background-color: rgba(255, 0, 0, 0.5);");
						builder.append("left: ")
								.append(offsetXField.getModelObject())
								.append("px; ");
						builder.append("top: ")
								.append(offsetYField.getModelObject())
								.append("px; ");
						builder.append("width: ")
								.append(widthField.getModelObject())
								.append("px; ");
						builder.append("height: ")
								.append(heightField.getModelObject())
								.append("px; ");

						orientationSelect.getModelObject().renderCSS(builder);

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
		resizableOptions.set("handles", Options.asString("nw, ne, sw, se"));
		// resizableOptions.set("aspectRatio", "1.0");
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
						widthField.setModelObject(width);
						heightField.setModelObject(height);

						target.add(offsetXField, offsetYField, widthField, heightField,
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
				mapService.addFogOfWarTriangle(map, widthField.getModelObject(), heightField.getModelObject(),
						offsetXField.getModelObject(),
						offsetYField.getModelObject(), orientationSelect.getModelObject());
			}
		};

		configureForm.add(widthField, heightField, orientationSelect);
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
