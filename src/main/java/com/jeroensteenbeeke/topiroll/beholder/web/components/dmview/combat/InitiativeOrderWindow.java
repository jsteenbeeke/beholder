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

import com.googlecode.wicket.jquery.ui.markup.html.link.AjaxSubmitLink;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.AjaxIconLink;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.ButtonType;
import com.jeroensteenbeeke.hyperion.icons.fontawesome.FontAwesome;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.InitiativeService;
import com.jeroensteenbeeke.topiroll.beholder.dao.InitiativeParticipantConditionDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.InitiativeParticipantDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.InitiativeParticipantConditionFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.InitiativeParticipantFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMModalWindow;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewCallback;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;

import javax.inject.Inject;
import java.util.List;

public class InitiativeOrderWindow extends DMModalWindow<MapView> {
	private static final long serialVersionUID = -7751191205271421987L;
	@Inject
	private InitiativeService initiativeService;

	@Inject
	private InitiativeParticipantDAO participationDAO;

	public InitiativeOrderWindow(String id, MapView view, DMViewCallback callback) {
		super(id, ModelMaker.wrap(view), "Initiative Order");

		WebMarkupContainer container = new WebMarkupContainer("container");
		container.setOutputMarkupId(true);

		addAjaxButton(target -> {
			initiativeService.clearNonPlayers(InitiativeOrderWindow.this.getModelObject());
			target.add(container);
			callback.refreshMenus(target);
		}).ofType(ButtonType.Danger).withLabel("Clear non-players");

		addAjaxButton(target -> {
			initiativeService.reroll(InitiativeOrderWindow.this.getModelObject());
			target.add(container);
			callback.refreshMenus(target);
		}).ofType(ButtonType.Danger).withLabel("Reroll");
		addAjaxButton(target -> {
			initiativeService.hideInitiative(InitiativeOrderWindow.this.getModelObject());
			callback.refreshMenus(target);
		}).ofType(ButtonType.Warning).withLabel("Hide");

		for (InitiativeLocation location : InitiativeLocation.values()) {
			addAjaxButton(target -> {
				initiativeService.showInitiative(
					InitiativeOrderWindow.this.getModelObject(),
					location);
				callback.refreshMenus(target);
			}).ofType(ButtonType.Success).withLabel(location.getPrettyName());
		}


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

			@Inject
			private InitiativeParticipantConditionDAO conditionDAO;

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

				InitiativeParticipantConditionFilter conditionFilter = new InitiativeParticipantConditionFilter();
				conditionFilter.participant(participant);
				conditionFilter.description().orderBy(true);

				item.add(new DataView<InitiativeParticipantCondition>("conditions", FilterDataProvider.of(conditionFilter, conditionDAO)) {

					private static final long serialVersionUID = 3893052636937876681L;

					@Override
					protected void populateItem(Item<InitiativeParticipantCondition> conditionItem) {
						InitiativeParticipantCondition condition = conditionItem.getModelObject();

						conditionItem.add(new Label("description", condition.getDescription()));
						conditionItem.add(new Label("turns", condition.getTurnsRemaining()).setVisible(condition.getTurnsRemaining() != null));
						conditionItem.add(
							new AjaxIconLink<>("edit", conditionItem.getModel(),
								FontAwesome.edit) {
								private static final long serialVersionUID = -7034858499613853766L;

								@Override
								public void onClick(AjaxRequestTarget target) {
									callback.removeModal(target);

									callback.createModalWindow(target,
										InitiativeParticipantConditionEditWindow::new,
										getModelObject());
								}
							});
						conditionItem.add(new AjaxIconLink<>("delete",
							conditionItem.getModel(), FontAwesome.trash) {
							private static final long serialVersionUID = -4100298936998032360L;

							@Override
							public void onClick(AjaxRequestTarget target) {
								conditionDAO.delete(getModelObject());
								callback.removeModal(target);
							}
						});
					}
				});

				item.add(new AjaxIconLink<>("select", item.getModel(),
					FontAwesome.location_arrow) {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						initiativeService.select(item.getModelObject());
						target.add(container);
						callback.refreshMenus(target);
					}
				}.setVisible(!participant.isSelected()));
				item.add(new AjaxIconLink<>("up", item.getModel(), FontAwesome.arrow_up) {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						initiativeService.moveUp(item.getModelObject());
						target.add(container);
						callback.refreshMenus(target);
					}

					@Override
					public boolean isVisible() {
						return super.isVisible() && initiativeService.canMoveUp(item.getModelObject());
					}
				});
				item.add(new AjaxIconLink<>("down", item.getModel(), FontAwesome.arrow_down) {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						initiativeService.moveDown(item.getModelObject());
						target.add(container);
						callback.refreshMenus(target);
					}

					@Override
					public boolean isVisible() {
						return super.isVisible() && initiativeService.canMoveDown(item.getModelObject());
					}
				});
				item.add(new AjaxIconLink<>("player", item.getModel(),
					FontAwesome.user) {

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
				item.add(new AjaxIconLink<>("nonplayer", item.getModel(),
					FontAwesome.chess_rook) {

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
				item.add(new AjaxIconLink<>("condition", item.getModel(),
					FontAwesome.paperclip) {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						InitiativeOrderWindow.this.setVisible(false);
						target.add(InitiativeOrderWindow.this);

						callback.createModalWindow(target,
							InitiativeParticipantConditionCreateWindow::new,
							getModelObject());
						callback.refreshMenus(target);
					}

				});
				item.add(new AjaxIconLink<>("delete", item.getModel(),
					FontAwesome.trash) {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						initiativeService.removeParticipant(item.getModelObject());
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
			new ListModel<>(List.of(InitiativeType.values())));
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

		getBody().add(AttributeModifier.replace("style", "height: 300px; overflow: auto;"));
	}
}
