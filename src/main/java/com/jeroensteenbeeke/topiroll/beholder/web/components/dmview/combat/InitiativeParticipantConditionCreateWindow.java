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
