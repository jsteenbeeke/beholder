package com.jeroensteenbeeke.topiroll.beholder.web.components.mapcontrol;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;

import com.google.common.collect.Lists;
import com.googlecode.wicket.jquery.ui.markup.html.link.AjaxSubmitLink;
import com.jeroensteenbeeke.hyperion.ducktape.web.components.TypedPanel;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.AjaxIconLink;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.GlyphIcon;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.InitiativeService;
import com.jeroensteenbeeke.topiroll.beholder.dao.InitiativeParticipantDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeLocation;
import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeParticipant;
import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeType;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.InitiativeParticipantFilter;

public class InitiativeOrderController extends TypedPanel<MapView> {

	private static final long serialVersionUID = 1L;

	@Inject
	private InitiativeService initiativeService;

	@Inject
	private InitiativeParticipantDAO participationDAO;

	public InitiativeOrderController(String id, MapView view) {
		super(id, ModelMaker.wrap(view));

		WebMarkupContainer container = new WebMarkupContainer("container");
		container.setOutputMarkupId(true);

		add(new AjaxLink<InitiativeLocation>("recalculate") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				initiativeService.reroll(
						InitiativeOrderController.this.getModelObject());
				target.add(container);

			}
		});

		add(new AjaxLink<InitiativeLocation>("hide") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				initiativeService.hideInitiative(
						InitiativeOrderController.this.getModelObject());
			}
		});

		add(new ListView<InitiativeLocation>("positions",
				Lists.newArrayList(InitiativeLocation.values())) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<InitiativeLocation> item) {
				item.add(new AjaxLink<InitiativeLocation>("show") {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						initiativeService.showInitiative(
								InitiativeOrderController.this.getModelObject(),
								item.getModelObject());

					}
				}.setBody(Model.of(item.getModelObject().getPrettyName())));
			}

		});
		add(container);

		InitiativeParticipantFilter filter = new InitiativeParticipantFilter();
		filter.view().equalTo(view);
		filter.total().orderBy(false);
		filter.score().orderBy(false);
		filter.name().orderBy(false);

		container.add(new DataView<InitiativeParticipant>("participants",
				FilterDataProvider.of(filter, participationDAO)) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<InitiativeParticipant> item) {
				InitiativeParticipant participant = item.getModelObject();

				item.add(new Label("name", participant.getName()));
				item.add(new Label("score", participant.getInitiativeType().formatBonus(participant.getScore())));
				item.add(new Label("initiative", participant.getTotal()));
				item.add(new AjaxIconLink<InitiativeParticipant>("delete",
						item.getModel(), GlyphIcon.trash) {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						initiativeService
								.removeParticipant(item.getModelObject());
						target.add(container);
					}
				});
			}
		});

		Form<InitiativeParticipant> addForm = new Form<>("participantForm");
		add(addForm);

		TextField<String> nameField = new TextField<String>("name", Model.of());
		nameField.setRequired(true);
		nameField.setOutputMarkupId(true);

		NumberTextField<Integer> scoreField = new NumberTextField<>("score",
				Model.of(0));
		scoreField.setOutputMarkupId(true);
		scoreField.setRequired(true);

		DropDownChoice<InitiativeType> typeField = new DropDownChoice<>("type",
				Model.of(InitiativeType.Normal),
				new ListModel<>(Lists.newArrayList(InitiativeType.values())));
		typeField.setOutputMarkupId(true);
		typeField.setRequired(true);

		addForm.add(nameField, scoreField, typeField);
		addForm.add(new AjaxSubmitLink("submit") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {

				super.onSubmit(target, form);

				initiativeService.addInitiative(getModelObject(),
						nameField.getModelObject(), scoreField.getModelObject(),
						typeField.getModelObject());
				
				nameField.setModel(Model.of(""));
				scoreField.setModel(Model.of(0));
				typeField.setModel(Model.of(InitiativeType.Normal));
				
				target.add(container, nameField, scoreField, typeField);
			}
		});
	}

}
