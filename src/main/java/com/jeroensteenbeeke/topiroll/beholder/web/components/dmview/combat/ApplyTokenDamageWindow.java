package com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.combat;

import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewCallback;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.model.Model;

import javax.inject.Inject;
import java.util.Optional;

public class ApplyTokenDamageWindow extends DMViewPanel<TokenInstance> {
	@Inject
	private MapService mapService;

	public ApplyTokenDamageWindow(String id, TokenInstance instance, DMViewCallback callback) {
		super(id, ModelMaker.wrap(instance));

		NumberTextField<Integer> damageField = new NumberTextField<>("damage", Model.of(0));

		Form<TokenInstance> damageForm = new Form<TokenInstance>("form") {
			@Override
			protected void onSubmit() {
				TokenInstance token = ApplyTokenDamageWindow.this.getModelObject();

				Integer newHP = Optional.ofNullable(token.getCurrentHitpoints()).map(h -> Math
						.max(0, h - damageField.getModelObject())).orElse(null);

				mapService.setTokenHP(token, newHP, token.getMaxHitpoints());

				if (newHP != null && newHP == 0) {
					mapService.hideToken(token);
				}
			}
		};

		damageForm.add(damageField);

		add(damageForm);

		add(new AjaxSubmitLink("submit", damageForm) {
			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				super.onSubmit(target);

				setVisible(false);

				target.add(ApplyTokenDamageWindow.this);
				target.appendJavaScript("$('#combat-modal').modal('hide');");

				callback.redrawMap(target);
				callback.removeModal(target);
			}
		});
	}

}
