package com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.combat;

import com.jeroensteenbeeke.hyperion.heinlein.web.components.ButtonType;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMModalWindow;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewCallback;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.model.Model;

import javax.inject.Inject;
import java.util.Optional;

public class ApplyTokenDamageWindow extends DMModalWindow<TokenInstance> {
	private static final long serialVersionUID = -3548970783637819306L;
	@Inject
	private MapService mapService;

	public ApplyTokenDamageWindow(String id, TokenInstance instance, DMViewCallback callback) {
		super(id, ModelMaker.wrap(instance), "Apply Damage");

		NumberTextField<Integer> damageField = new NumberTextField<>("damage", Model.of(0));

		Form<TokenInstance> damageForm = new Form<TokenInstance>("form") {
			private static final long serialVersionUID = 8502514465999134394L;

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

		addAjaxSubmitButton(target -> {
			setVisible(false);

			target.add(ApplyTokenDamageWindow.this);
			target.appendJavaScript("$('#combat-modal').modal('hide');");

			callback.redrawMap(target);
			callback.removeModal(target);
		}).forForm(damageForm).ofType(ButtonType.Primary).withLabel("Save changes");
	}

}
