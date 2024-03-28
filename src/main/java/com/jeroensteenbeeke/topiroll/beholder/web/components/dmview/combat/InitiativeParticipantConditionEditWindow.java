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
import com.jeroensteenbeeke.topiroll.beholder.dao.InitiativeParticipantConditionDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeParticipantCondition;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMModalWindow;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewCallback;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;

import jakarta.inject.Inject;

public class InitiativeParticipantConditionEditWindow extends DMModalWindow<InitiativeParticipantCondition> {
	private static final long serialVersionUID = -3287314849533291541L;

	@Inject
	private InitiativeParticipantConditionDAO conditionDAO;

	protected InitiativeParticipantConditionEditWindow(String id, InitiativeParticipantCondition condition, DMViewCallback callback) {
		super(id, ModelMaker.wrap(condition), "Edit condition");

		TextField<String> descriptionField = new TextField<>("description", Model.of(condition.getDescription()));
		descriptionField.setRequired(true);

		TextField<Integer> turnsRemainingField = new TextField<>("turns", Model.of(condition.getTurnsRemaining()), Integer.class);

		Form<InitiativeParticipantCondition> conditionForm = new Form<InitiativeParticipantCondition>("conditionForm") {
			private static final long serialVersionUID = 2244472224180856931L;
			@Inject
			private InitiativeService initiativeService;

			@Override
			protected void onSubmit() {
				super.onSubmit();

				String description = descriptionField.getModelObject();
				Integer turnsRemaining = turnsRemainingField.getModelObject();

				initiativeService.updateCondition(InitiativeParticipantConditionEditWindow.this.getModelObject(), description, turnsRemaining);
			}
		};

		conditionForm.add(descriptionField);
		conditionForm.add(turnsRemainingField);
		add(conditionForm);

		addAjaxSubmitButton(target -> {
			setVisible(false);

			target.add(InitiativeParticipantConditionEditWindow.this);
			callback.refreshMenus(target);
		}).forForm(conditionForm).ofType(ButtonType.Primary).withLabel("Update");
		addAjaxButton(target -> {
			conditionDAO.delete(getModelObject());
			setVisible(false);

			target.add(InitiativeParticipantConditionEditWindow.this);
			callback.refreshMenus(target);
		}).ofType(ButtonType.Danger).withLabel("Delete");
	}
}
