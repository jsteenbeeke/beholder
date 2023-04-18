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
package com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.combat;

import com.jeroensteenbeeke.hyperion.heinlein.web.components.ButtonType;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.hyperion.webcomponents.core.form.choice.LambdaRenderer;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.beans.SessionLogService;
import com.jeroensteenbeeke.topiroll.beholder.dao.InitiativeParticipantDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeParticipant;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenStatusEffect;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.InitiativeParticipantFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.BeholderSession;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMModalWindow;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewCallback;
import io.vavr.control.Option;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;

import javax.inject.Inject;
import java.util.List;

public class SetTokenStatusEffectWindow extends DMModalWindow<TokenInstance> {

	private static final long serialVersionUID = -5159850269544287237L;

	@Inject
	private MapService mapService;

	@Inject
	private SessionLogService sessionLogService;

	private DropDownChoice<TokenStatusEffect> statusEffectDropDownChoice;

	@Inject
	private InitiativeParticipantDAO initiativeParticipantDAO;

	private IModel<InitiativeParticipant> participantModel;

	public SetTokenStatusEffectWindow(String id, TokenInstance instance, DMViewCallback callback) {
		super(id, ModelMaker.wrap(instance), "Set a Status Effect");

		participantModel = () -> {
			InitiativeParticipantFilter filter = new InitiativeParticipantFilter();
			filter.selected(true);
			filter.view(callback.getView());

			return initiativeParticipantDAO.getUniqueByFilter(filter).getOrNull();
		};


		statusEffectDropDownChoice = new DropDownChoice<>("status",
														  Model.of(),
														  new ListModel<>(
															  List.of(TokenStatusEffect.values())),
														  LambdaRenderer.forEnum(TokenStatusEffect.class, Enum::name));
		statusEffectDropDownChoice.setRequired(true);

		Form<TokenInstance> statusForm = new Form<>("form") {

			private static final long serialVersionUID = -1366400863046730385L;

			@Override
			protected void onSubmit() {
				TokenInstance token = SetTokenStatusEffectWindow.this.getModelObject();
				TokenStatusEffect statusEffect = statusEffectDropDownChoice.getModelObject();
				mapService.setTokenStatusEffect(token, statusEffect);

				String entry = currentParticipant().filter(InitiativeParticipant::isPlayer)
					.map(InitiativeParticipant::getName)
					.map(name -> String.format("%s causes %s to become %s", name, token.getLabel(), statusEffect
						.name().toLowerCase()))
					.getOrElse(() -> String.format("%s becomes %s", token.getLabel(), statusEffect
						.name().toLowerCase()));
				BeholderSession
					.get()
					.user()
					.forEach(user -> sessionLogService.addSessionLogEntry(user, entry));

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

	public Option<InitiativeParticipant> currentParticipant() {
		return Option.of(participantModel.getObject());
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		participantModel.detach();
	}
}
