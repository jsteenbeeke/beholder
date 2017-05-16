/**
 * This file is part of Beholder
 * (C) 2016 Jeroen Steenbeeke
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jeroensteenbeeke.topiroll.beholder.web.components.mapcontrol;

import javax.inject.Inject;

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

		add(new AjaxLink<InitiativeLocation>("next") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				initiativeService.selectNext(
						InitiativeOrderController.this.getModelObject());
				target.add(container);

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

				Form<InitiativeParticipant> form = new Form<InitiativeParticipant>("total");
				NumberTextField<Integer> initiativeField =
						new NumberTextField<>("initiativeField", Model.of(participant.getTotal()), Integer.class);
				form.add(initiativeField);
				form.add(new AjaxSubmitLink("update") {
					@Override
					protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
						super.onSubmit(target, form);

						InitiativeParticipant participant = item.getModelObject();

						Integer total = initiativeField.getModelObject();
						if (total != null) {
							initiativeService.setParticipantTotal(participant, total);
							target.add(container);
						}
					}
				});
				item.add(form);
				item.add(new AjaxIconLink<InitiativeParticipant>("select",
						item.getModel(), GlyphIcon.screenshot) {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						initiativeService.select(item.getModelObject());
						target.add(container);
					}
				}.setVisible(!participant.isSelected()));
				item.add(new AjaxIconLink<InitiativeParticipant>("up",
						item.getModel(), GlyphIcon.arrowUp) {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						initiativeService.moveUp(item.getModelObject());
						target.add(container);
					}

					@Override
					public boolean isVisible() {
						return super.isVisible() && initiativeService
								.canMoveUp(item.getModelObject());
					}
				});
				item.add(new AjaxIconLink<InitiativeParticipant>("down",
						item.getModel(), GlyphIcon.arrowDown) {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						initiativeService.moveDown(item.getModelObject());
						target.add(container);
					}

					@Override
					public boolean isVisible() {
						return super.isVisible() && initiativeService
								.canMoveDown(item.getModelObject());
					}
				});
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

		Form<InitiativeParticipant> settingsForm = new Form<>("settingsForm");
		NumberTextField<Integer> marginField =
				new NumberTextField<Integer>("margin", Model.of(view.getInitiativeMargin()), Integer.class);
		settingsForm.add(marginField);
		settingsForm.add(new AjaxSubmitLink("submit") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {

				super.onSubmit(target, form);

				Integer margin = marginField.getModelObject();
				MapView view = getModelObject();

				initiativeService.setViewInitiativeMargin(view, margin);

				target.add(container, nameField, scoreField, typeField);
			}
		});
		add(settingsForm);
	}

}
