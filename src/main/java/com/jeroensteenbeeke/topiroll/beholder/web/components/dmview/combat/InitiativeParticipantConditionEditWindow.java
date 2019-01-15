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
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import javax.inject.Inject;

public class InitiativeParticipantConditionEditWindow extends DMModalWindow<InitiativeParticipantCondition> {
	@Inject
	private InitiativeParticipantConditionDAO conditionDAO;

	protected InitiativeParticipantConditionEditWindow(String id, InitiativeParticipantCondition condition, DMViewCallback callback) {
		super(id, ModelMaker.wrap(condition), "Edit condition");

		TextField<String> descriptionField = new TextField<>("description", Model.of(condition.getDescription()));
		descriptionField.setRequired(true);

		TextField<Integer> turnsRemainingField = new TextField<>("turns", Model.of(condition.getTurnsRemaining()), Integer.class);

		Form<InitiativeParticipantCondition> conditionForm = new Form<InitiativeParticipantCondition>("conditionForm") {
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
