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
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;

import com.google.common.collect.Lists;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableAdapter;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableBehavior;
import com.jeroensteenbeeke.hyperion.ducktape.web.renderer.LambdaRenderer;
import com.jeroensteenbeeke.hyperion.heinlein.web.resources.TouchPunchJavaScriptReference;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.hyperion.util.ImageUtil;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenBorderType;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenDefinition;
import com.jeroensteenbeeke.topiroll.beholder.web.components.ImageContainer;
import com.jeroensteenbeeke.topiroll.beholder.web.components.MapEditSubmitPanel;
import com.jeroensteenbeeke.topiroll.beholder.web.components.SubmitPanel;
import com.jeroensteenbeeke.topiroll.beholder.web.resources.MapResource;
import com.jeroensteenbeeke.topiroll.beholder.web.resources.TokenResource;

public class AddTokenInstance2Page extends AuthenticatedPage {
	private static final long serialVersionUID = 1L;

	private TextField<String> badgeField;

	private DropDownChoice<TokenBorderType> borderSelect;

	private NumberTextField<Integer> offsetXField;

	private NumberTextField<Integer> offsetYField;

	private IModel<ScaledMap> mapModel;

	private IModel<TokenDefinition> tokenModel;

	public AddTokenInstance2Page(ScaledMap map, TokenDefinition token,
			int amount) {
		super("Configure map");

		this.mapModel = ModelMaker.wrap(map);
		this.tokenModel = ModelMaker.wrap(token);

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

		badgeField = new TextField<String>("badge", Model.of(""));

		borderSelect = new DropDownChoice<>("border",
				Model.of(TokenBorderType.Neutral),
				new ListModel<TokenBorderType>(
						Lists.newArrayList(TokenBorderType.values())),
				LambdaRenderer.forEnum(TokenBorderType.class, Enum::name));
		borderSelect.setRequired(true);

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

		final ImageContainer previewImage = new ImageContainer("preview",
				new ResourceReference(
						String.format("preview-%d", map.getId())) {
					private static final long serialVersionUID = 1L;

					@Override
					public IResource getResource() {
						return new MapResource(mapModel.getObject().getId());
					}

				}, dimensions);
		previewImage.setOutputMarkupId(true);

		Image areaMarker = new Image("areaMarker",
				new TokenResource(token.getId()));
		areaMarker.add(AttributeModifier.replace("style", String.format(
				"background-color: rgba(255, 0, 0, 0.5); width: %dpx; height: %dpx; left: %dpx; top: %dpx;",
				map.getSquareSize(), map.getSquareSize(),
				offsetXField.getModelObject(), offsetYField.getModelObject())));

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

		previewImage.add(areaMarker);

		Form<ScaledMap> configureForm = new Form<ScaledMap>("configureForm",
				mapModel) {
			private static final long serialVersionUID = 1L;

			@Inject
			private MapService mapService;

			@Override
			protected void onSubmit() {
				ScaledMap map = mapModel.getObject();

				mapService.createTokenInstance(tokenModel.getObject(), map,
						borderSelect.getModelObject(),
						offsetXField.getModelObject(),
						offsetYField.getModelObject(),
						badgeField.getModelObject());
			}
		};

		configureForm.add(badgeField);
		configureForm.add(borderSelect);
		configureForm.add(offsetXField);
		configureForm.add(offsetYField);

		add(configureForm);

		add(previewImage);

		if (amount == 1) {
			add(new MapEditSubmitPanel("submit", configureForm));
		} else {
			add(new SubmitPanel<ScaledMap>("submit", configureForm, m -> {
				setResponsePage(new AddTokenInstance2Page(m,
						tokenModel.getObject(), amount - 1));
			}));
		}
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

	public NumberTextField<Integer> getOffsetXField() {
		return offsetXField;
	}

	public NumberTextField<Integer> getOffsetYField() {
		return offsetYField;
	}
}
