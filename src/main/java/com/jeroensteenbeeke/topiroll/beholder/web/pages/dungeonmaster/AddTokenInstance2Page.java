/**
 * This file is part of Beholder
 * (C) 2016 Jeroen Steenbeeke
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster;

import com.google.common.collect.Lists;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableAdapter;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableBehavior;
import com.googlecode.wicket.jquery.ui.markup.html.link.SubmitLink;
import com.jeroensteenbeeke.hyperion.ducktape.web.renderer.LambdaRenderer;
import com.jeroensteenbeeke.hyperion.heinlein.web.resources.TouchPunchJavaScriptReference;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.entities.*;
import com.jeroensteenbeeke.topiroll.beholder.web.components.AbstractMapPreview;
import com.jeroensteenbeeke.topiroll.beholder.web.components.MapEditSubmitPanel;
import com.jeroensteenbeeke.topiroll.beholder.web.components.SubmitPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;

import javax.inject.Inject;

public class AddTokenInstance2Page extends AuthenticatedPage {
	private static final long serialVersionUID = 1L;

	private TextField<String> badgeField;

	private DropDownChoice<TokenBorderType> borderSelect;

	private NumberTextField<Integer> offsetXField;

	private NumberTextField<Integer> offsetYField;

	private IModel<ScaledMap> mapModel;

	private IModel<TokenDefinition> tokenModel;

	public AddTokenInstance2Page(ScaledMap map, TokenDefinition token, TokenBorderType borderType,
								 int current, int total) {
		super("Configure map");

		this.mapModel = ModelMaker.wrap(map);
		this.tokenModel = ModelMaker.wrap(token);

		add(new Link<BeholderUser>("back") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				onBackButtonClicked();

			}


		});

		final int imageWidth = map.getBasicWidth();
		final int imageHeight = map.getBasicHeight();

		badgeField = new TextField<>("badge", Model.of(String.format("%s %d", token.getName(), current)));

		borderSelect = new DropDownChoice<>("border",
				Model.of(borderType),
				new ListModel<>(
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

		final AbstractMapPreview previewImage =
				new AbstractMapPreview("preview", map, Math.min(1200, map.getBasicWidth())) {
					@Override
					protected void addOnDomReadyJavaScript(String canvasId, StringBuilder js, double factor) {
						getMap().getTokens().stream()
								.map(t -> String.format("previewToken('%s', %s);\n", canvasId, t.toPreview(factor)))
								.forEach(js::append);
					}
				};
		previewImage.setOutputMarkupId(true);

		ContextImage areaMarker = new ContextImage("areaMarker",
				"tokens/" + token.getId());
		int wh = previewImage.translateToScaledImageSize(map.getSquareSize() * token.getDiameterInSquares());
		areaMarker.add(AttributeModifier.replace("style", String.format(
				"padding: 0px; width: %dpx; height: %dpx; max-width: %dpx !important; max-height:" +
						" %dpx !important; left:" +
						" %dpx; top: %dpx; border-radius: " +
						"100%%; border: 1px solid #000000;",

				wh, wh, wh, wh,
				previewImage.translateToScaledImageSize(offsetXField.getModelObject()),
				previewImage.translateToScaledImageSize(offsetYField.getModelObject()))));

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

						offsetXField.setModelObject(previewImage.translateToRealImageSize(left));
						offsetYField.setModelObject(previewImage.translateToRealImageSize(top));

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

		createSubmitPanel(current, total, configureForm);
	}

	protected void createSubmitPanel(int current, int total,
									 Form<ScaledMap> configureForm) {
		if (current == total) {
			add(new MapEditSubmitPanel("submit", configureForm));
		} else {
			add(new SubmitPanel<ScaledMap>("submit", configureForm, m -> {
				setResponsePage(new AddTokenInstance2Page(m,
						tokenModel.getObject(), borderSelect.getModelObject(), current + 1, total));
			}) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void decorateLink(SubmitLink submitLink) {
					super.decorateLink(submitLink);

					submitLink.setBody(Model.of("Next"));
				}

			});
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
		tokenModel.detach();
	}

	protected void onBackButtonClicked() {
		setResponsePage(new ViewMapPage(mapModel.getObject()));
	}

	public NumberTextField<Integer> getOffsetXField() {
		return offsetXField;
	}

	public NumberTextField<Integer> getOffsetYField() {
		return offsetYField;
	}
}
