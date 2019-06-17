/**
 * This file is part of Beholder
 * (C) 2016-2019 Jeroen Steenbeeke
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
package com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.combat;

import com.google.common.collect.Lists;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.ButtonType;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.hyperion.webcomponents.core.form.choice.LambdaRenderer;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenStatusEffect;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMModalWindow;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewCallback;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;

import javax.inject.Inject;
public class SetTokenStatusEffectWindow extends DMModalWindow<TokenInstance> {

	private static final long serialVersionUID = -5159850269544287237L;

	@Inject
	private MapService mapService;
	
	private DropDownChoice<TokenStatusEffect> statusEffectDropDownChoice;

	public SetTokenStatusEffectWindow(String id, TokenInstance instance, DMViewCallback callback) {
		super(id, ModelMaker.wrap(instance), "Set a Status Effect");

		statusEffectDropDownChoice = new DropDownChoice<>("status",
				Model.of(),
				new ListModel<>(
						Lists.newArrayList(TokenStatusEffect.values())),
				LambdaRenderer.forEnum(TokenStatusEffect.class, Enum::name));
		statusEffectDropDownChoice.setRequired(true);

		Form<TokenInstance> statusForm = new Form<TokenInstance>("form") {

			private static final long serialVersionUID = -1366400863046730385L;

			@Override
			protected void onSubmit() {
				TokenInstance token = SetTokenStatusEffectWindow.this.getModelObject();
				mapService.setTokenStatusEffect(token, statusEffectDropDownChoice.getModelObject());
			}
		};

		statusForm.add(statusEffectDropDownChoice);

		add(statusForm);

		addAjaxSubmitButton(target -> {
			setVisible(false);

			target.add(SetTokenStatusEffectWindow.this);
			target.appendJavaScript("$('#combat-modal').modal('hide');");

			callback.redrawMap(target);
			callback.removeModal(target);
		}).forForm(statusForm).ofType(ButtonType.Primary).withLabel("Save changes");
	}

}
