package com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.combat;

import com.google.common.collect.Lists;
import com.googlecode.wicket.jquery.ui.markup.html.link.AjaxSubmitLink;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.AjaxIconLink;
import com.jeroensteenbeeke.hyperion.icons.fontawesome.FontAwesome;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.InitiativeService;
import com.jeroensteenbeeke.topiroll.beholder.dao.InitiativeParticipantDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeLocation;
import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeParticipant;
import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeType;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.InitiativeParticipantFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewCallback;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewPanel;
import org.apache.wicket.AttributeModifier;
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
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;

import javax.inject.Inject;

public class InitiativeOrderWindow extends DMViewPanel<MapView> {
	private static final long serialVersionUID = -7751191205271421987L;
	@Inject
	private InitiativeService initiativeService;

	@Inject
	private InitiativeParticipantDAO participationDAO;

	public InitiativeOrderWindow(String id, MapView view, DMViewCallback callback) {
		super(id);

		setModel(ModelMaker.wrap(view));

		WebMarkupContainer container = new WebMarkupContainer("container");
		container.setOutputMarkupId(true);

		add(new AjaxLink<InitiativeLocation>("recalculate") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				initiativeService.reroll(
						InitiativeOrderWindow.this.getModelObject());
				target.add(container);
				callback.refreshMenus(target);
			}
		});

		add(new AjaxLink<InitiativeLocation>("hide") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				initiativeService.hideInitiative(
						InitiativeOrderWindow.this.getModelObject());
				callback.refreshMenus(target);
			}
		});

		add(new AjaxLink<InitiativeLocation>("clear") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				initiativeService.clearNonPlayers(
						InitiativeOrderWindow.this.getModelObject());
				target.add(container);
				callback.refreshMenus(target);
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
								InitiativeOrderWindow.this.getModelObject(),
								item.getModelObject());
						callback.refreshMenus(target);

					}
				}.setBody(Model.of(item.getModelObject().getPrettyName())));
			}

		});
		add(container);

		InitiativeParticipantFilter filter = new InitiativeParticipantFilter();
		filter.view().equalTo(view);
		filter.total().orderBy(false);
		filter.score().orderBy(false);
		filter.orderOverride().orderBy(true);
		filter.name().orderBy(false);

		container.add(new DataView<InitiativeParticipant>("participants",
				FilterDataProvider.of(filter, participationDAO)) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<InitiativeParticipant> item) {
				InitiativeParticipant participant = item.getModelObject();

				Label nameLabel = new Label("name", participant.getName());
				item.add(AttributeModifier.replace("class",
						new LoadableDetachableModel<String>() {
							private static final long serialVersionUID = 1L;

							@Override
							protected String load() {
								if (participant.isSelected()) {
									return "bg-success";
								}
								return "";
							}
						}));

				item.add(nameLabel);
				item.add(new Label("score", participant.getInitiativeType()
						.formatBonus(participant.getScore())));
				item.add(new Label("type", participant.isPlayer() ? "Player" : "DM-controlled"));

				Form<InitiativeParticipant> form = new Form<InitiativeParticipant>("total");
				NumberTextField<Integer> initiativeField =
						new NumberTextField<>("initiativeField", Model.of(participant.getTotal()), Integer.class);
				form.add(initiativeField);

				form.add(new AjaxSubmitLink("update") {
					private static final long serialVersionUID = -4319809501288623816L;

					@Override
					protected void onSubmit(AjaxRequestTarget target) {
						super.onSubmit(target);

						InitiativeParticipant participant = item.getModelObject();

						Integer total = initiativeField.getModelObject();
						if (total != null) {
							initiativeService.setParticipantTotal(participant, total);
							target.add(container);
						}
						callback.refreshMenus(target);
					}
				});
				item.add(form);
				item.add(new AjaxIconLink<InitiativeParticipant>("select",
						item.getModel(), FontAwesome.location_arrow) {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						initiativeService.select(item.getModelObject());
						target.add(container);
						callback.refreshMenus(target);
					}
				}.setVisible(!participant.isSelected()));
				item.add(new AjaxIconLink<InitiativeParticipant>("up",
						item.getModel(), FontAwesome.arrow_up) {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						initiativeService.moveUp(item.getModelObject());
						target.add(container);
						callback.refreshMenus(target);
					}

					@Override
					public boolean isVisible() {
						return super.isVisible() && initiativeService
								.canMoveUp(item.getModelObject());
					}
				});
				item.add(new AjaxIconLink<InitiativeParticipant>("down",
						item.getModel(), FontAwesome.arrow_down) {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						initiativeService.moveDown(item.getModelObject());
						target.add(container);
						callback.refreshMenus(target);
					}

					@Override
					public boolean isVisible() {
						return super.isVisible() && initiativeService
								.canMoveDown(item.getModelObject());
					}
				});
				item.add(new AjaxIconLink<InitiativeParticipant>("player",
						item.getModel(), FontAwesome.user) {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						initiativeService.markAsPlayer(item.getModelObject());
						target.add(container);
						callback.refreshMenus(target);
					}

					@Override
					public boolean isVisible() {
						return super.isVisible() && !item.getModelObject().isPlayer();
					}
				});
				item.add(new AjaxIconLink<InitiativeParticipant>("nonplayer",
						item.getModel(), FontAwesome.chess_rook) {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						initiativeService.markAsNonPlayer(item.getModelObject());
						target.add(container);
						callback.refreshMenus(target);

					}

					@Override
					public boolean isVisible() {
						return super.isVisible() && item.getModelObject().isPlayer();

					}
				});
				item.add(new AjaxIconLink<InitiativeParticipant>("delete",
						item.getModel(), FontAwesome.trash) {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						initiativeService
								.removeParticipant(item.getModelObject());
						target.add(container);
						callback.refreshMenus(target);
					}

					@Override
					public boolean isVisible() {
						return super.isVisible() && !item.getModelObject().isPlayer();
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
			protected void onSubmit(AjaxRequestTarget target) {

				super.onSubmit(target);

				initiativeService.addInitiative(getModelObject(),
						nameField.getModelObject(), scoreField.getModelObject(),
						typeField.getModelObject());

				nameField.setModel(Model.of(""));
				scoreField.setModel(Model.of(0));
				typeField.setModel(Model.of(InitiativeType.Normal));

				target.add(container, nameField, scoreField, typeField);
				callback.refreshMenus(target);
			}
		});

		Form<InitiativeParticipant> settingsForm = new Form<>("settingsForm");
		NumberTextField<Integer> marginField =
				new NumberTextField<Integer>("margin", Model.of(view.getInitiativeMargin()), Integer.class);
		settingsForm.add(marginField);
		settingsForm.add(new AjaxSubmitLink("submit") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {

				super.onSubmit(target);

				Integer margin = marginField.getModelObject();
				MapView view = getModelObject();

				initiativeService.setViewInitiativeMargin(view, margin);

				target.add(container, nameField, scoreField, typeField);

				callback.refreshMenus(target);
			}
		});
		add(settingsForm);
	}
}
