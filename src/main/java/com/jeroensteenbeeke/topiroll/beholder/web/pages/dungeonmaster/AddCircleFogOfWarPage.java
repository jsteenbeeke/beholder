/*
 * This file is part of Beholder
 * Copyright (C) 2016 - 2023 Jeroen Steenbeeke
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

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableAdapter;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableBehavior;
import com.googlecode.wicket.jquery.ui.interaction.resizable.ResizableAdapter;
import com.googlecode.wicket.jquery.ui.interaction.resizable.ResizableBehavior;
import com.jeroensteenbeeke.hyperion.heinlein.web.resources.TouchPunchJavaScriptReference;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.web.components.AbstractMapPreview;
import com.jeroensteenbeeke.topiroll.beholder.web.components.MapEditSubmitPanel;
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

import javax.inject.Inject;

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

		final int imageWidth = map.getBasicWidth();
		final int imageHeight = map.getBasicHeight();

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

		final AbstractMapPreview previewImage =
				new AbstractMapPreview("preview", map, Math.min(1024, map.getBasicWidth())) {
					private static final long serialVersionUID = 117047744683586159L;

					@Override
					protected void addOnDomReadyJavaScript(String canvasId, StringBuilder js, double factor) {
						getMap().getAllShapes().stream()
								.map(s -> s.visit(new FogOfWarPreviewRenderer(canvasId, factor)))
								.forEach(js::append);
					}
				};


		WebMarkupContainer areaMarker = new WebMarkupContainer("areaMarker");
		areaMarker.add(AttributeModifier.replace("style", String.format(
				"background-color: rgba(255, 0, 0, 0.5); border-radius: 100%%; width: %dpx; height: %dpx; left: %dpx; " +
						"top: %dpx;",
				previewImage.translateToScaledImageSize(radiusField.getModelObject() * 2),
				previewImage.translateToScaledImageSize(radiusField.getModelObject() * 2),
				previewImage.translateToScaledImageSize(offsetXField.getModelObject()),
				previewImage.translateToScaledImageSize(offsetYField.getModelObject()))));

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

						offsetXField.setModelObject(previewImage.translateToRealImageSize(left));
						offsetYField.setModelObject(previewImage.translateToRealImageSize(top));

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

				offsetXField.setModelObject(previewImage.translateToRealImageSize(left));
				offsetYField.setModelObject(previewImage.translateToRealImageSize(top));
				radiusField.setModelObject(previewImage.translateToRealImageSize(radius));

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
