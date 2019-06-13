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

import com.jeroensteenbeeke.hyperion.heinlein.web.components.ButtonType;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.InitiativeService;
import com.jeroensteenbeeke.topiroll.beholder.dao.InitiativeParticipantConditionDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeParticipant;
import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeParticipantCondition;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMModalWindow;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewCallback;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import javax.inject.Inject;

public class InitiativeParticipantConditionCreateWindow extends DMModalWindow<InitiativeParticipant> {
	private static final long serialVersionUID = 6002209695034331901L;

	@Inject
	private InitiativeParticipantConditionDAO conditionDAO;

	protected InitiativeParticipantConditionCreateWindow(String id, InitiativeParticipant participant, DMViewCallback callback) {
		super(id, ModelMaker.wrap(participant), "Add condition");

		TextField<String> descriptionField = new TextField<>("description", Model.of());
		descriptionField.setRequired(true);

		TextField<Integer> turnsRemainingField = new TextField<>("turns", Model.of(), Integer.class);

		Form<InitiativeParticipantCondition> conditionForm = new Form<InitiativeParticipantCondition>("conditionForm") {
			private static final long serialVersionUID = -1627463188051664546L;
			@Inject
			private InitiativeService initiativeService;

			@Override
			protected void onSubmit() {
				super.onSubmit();

				String description = descriptionField.getModelObject();
				Integer turnsRemaining = turnsRemainingField.getModelObject();

				initiativeService.createCondition(InitiativeParticipantConditionCreateWindow.this.getModelObject(), description, turnsRemaining);
			}
		};

		conditionForm.add(descriptionField);
		conditionForm.add(turnsRemainingField);
		add(conditionForm);

		addAjaxSubmitButton(target -> {
			setVisible(false);

			target.add(this);
			callback.refreshMenus(target);
		}).forForm(conditionForm).ofType(ButtonType.Primary).withLabel("Update");
	}
}
