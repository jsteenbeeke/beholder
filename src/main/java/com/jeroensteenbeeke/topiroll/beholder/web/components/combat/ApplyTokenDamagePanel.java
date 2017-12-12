package com.jeroensteenbeeke.topiroll.beholder.web.components.combat;

import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.model.Model;

import javax.inject.Inject;
import java.util.Optional;

public class ApplyTokenDamagePanel extends CombatModePanel<TokenInstance> {
	@Inject
	private MapService mapService;

	public ApplyTokenDamagePanel(String id, TokenInstance instance, CombatModeCallback callback) {
		super(id, ModelMaker.wrap(instance));

		NumberTextField<Integer> damageField = new NumberTextField<>("damage", Model.of(0));

		Form<TokenInstance> damageForm = new Form<TokenInstance>("form") {
			@Override
			protected void onSubmit() {
				TokenInstance token = ApplyTokenDamagePanel.this.getModelObject();

				Integer newHP = Optional.ofNullable(token.getCurrentHitpoints()).map(h -> Math
						.max(0, h-damageField.getModelObject())).orElse(null);

				mapService.setTokenHP(token, newHP, token.getMaxHitpoints());
			}
		};

		damageForm.add(damageField);

		add(damageForm);

		add(new AjaxSubmitLink("submit") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				super.onSubmit(target, form);

				setVisible(false);

				target.add(ApplyTokenDamagePanel.this);
			}
		});
	}

}
