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
import com.jeroensteenbeeke.topiroll.beholder.beans.InitiativeService;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.beans.SessionLogService;
import com.jeroensteenbeeke.topiroll.beholder.dao.InitiativeParticipantDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeParticipant;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.InitiativeParticipantFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.BeholderSession;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMModalWindow;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewCallback;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewPanel;
import io.vavr.control.Option;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import javax.inject.Inject;
import java.util.Optional;

public class ApplyTokenDamageWindow extends DMModalWindow<TokenInstance> {
	private static final long serialVersionUID = -3548970783637819306L;
	@Inject
	private MapService mapService;

	@Inject
	private SessionLogService sessionLogService;

	@Inject
	private InitiativeParticipantDAO initiativeParticipantDAO;

	private IModel<InitiativeParticipant> participantModel;

	public ApplyTokenDamageWindow(String id, TokenInstance instance, DMViewCallback callback) {
		super(id, ModelMaker.wrap(instance), "Apply Damage");

		participantModel = () -> {
			InitiativeParticipantFilter filter = new InitiativeParticipantFilter();
			filter.selected(true);
			filter.view(callback.getView());

			return initiativeParticipantDAO.getUniqueByFilter(filter).getOrNull();
		};

		NumberTextField<Integer> damageField = new NumberTextField<>("damage", Model.of(0));
		TextField<String> damageDescriptionField = new TextField<>("description", Model.of(""));


		Form<TokenInstance> damageForm = new Form<TokenInstance>("form") {
			private static final long serialVersionUID = 8502514465999134394L;

			@Override
			protected void onSubmit() {
				TokenInstance token = ApplyTokenDamageWindow.this.getModelObject();

				Integer damage = damageField.getModelObject();

				Integer newHP = Optional.ofNullable(token.getCurrentHitpoints()).map(h -> Math
					.max(0, h - damage)).orElse(null);

				mapService.setTokenHP(token, newHP, token.getMaxHitpoints());

				boolean lethal = false;

				if (newHP != null && newHP == 0) {
					mapService.hideToken(token);
					lethal = true;
				}

				if (damageDescriptionField.isEnabled()) {
					String description = damageDescriptionField.getModelObject();

					BeholderUser user = BeholderSession.get().getUser();

					if (user != null) {
						sessionLogService.addSessionLogEntry(user, description, currentParticipant(),
															 token, damage, lethal);
					}
				}
			}
		};

		damageForm.add(damageField, damageDescriptionField);

		add(damageForm);

		addAjaxSubmitButton(target -> {
			setVisible(false);

			target.add(ApplyTokenDamageWindow.this);
			target.appendJavaScript("$('#combat-modal').modal('hide');");

			callback.redrawMap(target);
			callback.removeModal(target);
		}).forForm(damageForm).ofType(ButtonType.Primary).withLabel("Save changes");
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
