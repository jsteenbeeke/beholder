package com.jeroensteenbeeke.topiroll.beholder.web.components.combat;

import com.google.common.collect.Lists;
import com.jeroensteenbeeke.hyperion.ducktape.web.renderer.LambdaRenderer;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.TokenDefinitionDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.TokenInstanceDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenBorderType;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenDefinition;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.TokenDefinitionFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.TokenInstanceFilter;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.awt.*;

public class CreateTokenPanel extends CombatModePanel<ScaledMap> {
	@Inject
	private MapService mapService;

	@Inject
	private TokenDefinitionDAO definitionDAO;

	@Inject
	private TokenInstanceDAO instanceDAO;

	public CreateTokenPanel(String id, ScaledMap map, CombatModeCallback callback) {
		super(id, ModelMaker.wrap(map));

		final Point point = callback.getClickedLocation();

		final int x = point.x;
		final int y = point.y;

		final TextField<String> labelField = new TextField<>("label", Model.of(""));
		labelField.setOutputMarkupId(true);
		labelField.setRequired(true);

		TokenDefinitionFilter filter = new TokenDefinitionFilter();
		filter.owner(map.getOwner());
		filter.name().orderBy(true);

		final DropDownChoice<TokenDefinition> definitions = new DropDownChoice<>("type",
				ModelMaker.wrap((TokenDefinition) null, true),
				ModelMaker.wrapList(definitionDAO.findByFilter(filter), false),
				LambdaRenderer.of(TokenDefinition::getName)
				);
		definitions.setRequired(true);
		definitions.add(new AjaxFormComponentUpdatingBehavior("change") {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				if (StringUtils.isEmpty(labelField.getModelObject())) {
					TokenDefinition def = definitions.getModelObject();

					TokenInstanceFilter filter = new TokenInstanceFilter();
					filter.definition(def);
					filter.map(CreateTokenPanel.this.getModelObject());

					labelField.setModelObject(def.getName() + " "+ (1+instanceDAO.countByFilter(filter)));

					target.add(labelField);
				}
			}
		});

		final DropDownChoice<TokenBorderType> borderTypeSelect = new DropDownChoice<>("bordertype", Model.of(TokenBorderType.Enemy),
				Lists.newArrayList(TokenBorderType.values()),
				LambdaRenderer.of(TokenBorderType::name)
				);
		borderTypeSelect.setRequired(true);

		final NumberTextField<Integer> hpField = new NumberTextField<>("hp", Model.of(),
				Integer.class);

		Form<ScaledMap> tokenForm = new Form<ScaledMap>("form", ModelMaker.wrap(map)) {
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
		add(new AjaxSubmitLink("submit", tokenForm) {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				super.onSubmit(target, form);

				setVisible(false);

				target.add(CreateTokenPanel.this);
				target.appendJavaScript("$('#combat-modal').modal('hide');");

				callback.redrawMap(target);
				callback.removeModal(target);
			}
		});
	}
}