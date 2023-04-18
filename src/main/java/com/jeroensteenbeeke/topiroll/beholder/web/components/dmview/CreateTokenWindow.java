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
package com.jeroensteenbeeke.topiroll.beholder.web.components.dmview;

import com.jeroensteenbeeke.hyperion.heinlein.web.components.ButtonType;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.hyperion.webcomponents.core.form.choice.LambdaRenderer;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.TokenDefinitionDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.TokenInstanceDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenBorderType;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenDefinition;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.TokenDefinitionFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.TokenInstanceFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMModalWindow;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewCallback;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;

public class CreateTokenWindow extends DMModalWindow<ScaledMap> {
	private static final long serialVersionUID = 2854668991094369943L;
	@Inject
	private MapService mapService;

	@Inject
	private TokenDefinitionDAO definitionDAO;

	@Inject
	private TokenInstanceDAO instanceDAO;

	public CreateTokenWindow(String id, ScaledMap map, DMViewCallback callback) {
		super(id, ModelMaker.wrap(map), "Add token");

		final Point point = callback.getClickedLocation().orElseThrow(CannotCreateModalWindowException::new);

		final int x = point.x;
		final int y = point.y;

		final TextField<String> labelField = new TextField<>("label", Model.of(""));
		labelField.setOutputMarkupId(true);
		labelField.setRequired(true);

		TokenDefinitionFilter filter = new TokenDefinitionFilter();
		filter.owner(map.getOwner());
		map.getOwner().activeCampaign().peek(c -> filter.campaign().isNull().orCampaign(c));
		filter.name().orderBy(true);

		final DropDownChoice<TokenDefinition> definitions = new DropDownChoice<>("type",
			ModelMaker.wrap(TokenDefinition.class),
			ModelMaker.wrapList(definitionDAO.findByFilter(filter).toJavaList()),
			LambdaRenderer.of(TokenDefinition::getName)
		);
		definitions.setRequired(true);
		definitions.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 31162114424383849L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				if (StringUtils.isEmpty(labelField.getModelObject())) {
					TokenDefinition def = definitions.getModelObject();

					TokenInstanceFilter filter = new TokenInstanceFilter();
					filter.definition(def);
					filter.map(CreateTokenWindow.this.getModelObject());

					labelField.setModelObject(def.getName() + " " + (1 + instanceDAO.countByFilter(filter)));

					target.add(labelField);
				}
			}
		});

		final DropDownChoice<TokenBorderType> borderTypeSelect = new DropDownChoice<>("bordertype", Model.of(TokenBorderType.Enemy),
			List.of(TokenBorderType.values()),
			LambdaRenderer.of(TokenBorderType::name)
		);
		borderTypeSelect.setRequired(true);

		final NumberTextField<Integer> hpField = new NumberTextField<>("hp", Model.of(),
			Integer.class);

		Form<ScaledMap> tokenForm = new Form<ScaledMap>("form", ModelMaker.wrap(map)) {
			private static final long serialVersionUID = 5345121083973575122L;

			@Override
			protected void onSubmit() {
				String label = labelField.getModelObject();
				TokenDefinition def = definitions.getModelObject();
				TokenBorderType type = borderTypeSelect.getModelObject();

				ScaledMap map = getModelObject();

				int diam = def.getDiameterInSquares() * map.getSquareSize();
				int rad = diam / 2;

				TokenInstance instance = mapService.createTokenInstance(def, map,
					type, x - rad, y - rad, label);
				mapService.setTokenHP(instance, hpField.getModelObject(), hpField.getModelObject());

			}
		};

		tokenForm.add(definitions);
		tokenForm.add(labelField);
		tokenForm.add(borderTypeSelect);
		tokenForm.add(hpField);

		add(tokenForm);

		addAjaxSubmitButton(target -> {
			setVisible(false);

			target.add(CreateTokenWindow.this);
			target.appendJavaScript("$('#combat-modal').modal('hide');");

			callback.redrawMap(target);
			callback.removeModal(target);
		}).forForm(tokenForm).ofType(ButtonType.Primary).withLabel("Add token");
	}
}
