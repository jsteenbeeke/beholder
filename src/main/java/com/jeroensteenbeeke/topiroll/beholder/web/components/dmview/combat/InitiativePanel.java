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

import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.InitiativeService;
import com.jeroensteenbeeke.topiroll.beholder.dao.InitiativeParticipantConditionDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.InitiativeParticipantDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeParticipant;
import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeParticipantCondition;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.InitiativeParticipantConditionFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.InitiativeParticipantFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewCallback;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import jakarta.inject.Inject;
import java.util.List;

public class InitiativePanel extends DMViewPanel<MapView> {
	private static final String UNKNOWN = "-";

	private static final long serialVersionUID = 7041791285729658904L;

	private final Label current;

	@Inject
	private InitiativeParticipantDAO initiativeDAO;

	@Inject
	private InitiativeParticipantConditionDAO conditionDAO;

	@Inject
	private InitiativeService initiativeService;

	private IModel<InitiativeParticipant> currentParticipantModel;

	public InitiativePanel(String id, MapView view, DMViewCallback callback) {
		super(id, ModelMaker.wrap(view));

		setOutputMarkupId(true);

		currentParticipantModel = new LoadableDetachableModel<>() {
			private static final long serialVersionUID = 903788535948273973L;

			@Override
			protected InitiativeParticipant load() {
				MapView view = getModelObject();
				return initiativeDAO.getUniqueByFilter(
						new InitiativeParticipantFilter().view(view).selected(true))
					.getOrNull();
			}
		};

		IModel<String> currentParticipantNameModel = new LoadableDetachableModel<>() {
			private static final long serialVersionUID = 5876679397431324467L;

			@Override
			protected String load() {
				MapView view = getModelObject();
				return initiativeDAO.getUniqueByFilter(
						new InitiativeParticipantFilter().view(view).selected(true))
					.map(InitiativeParticipant::getName).getOrElse(UNKNOWN);
			}
		};

		add(current = new Label("current", currentParticipantNameModel) {
			private static final long serialVersionUID = -4939018390661246323L;

			@Override
			public boolean isVisible() {
				return super.isVisible() && !UNKNOWN.equals(getDefaultModelObject());
			}
		});
		current.setOutputMarkupId(true);

		add(new AjaxLink<>("initiative", ModelMaker.wrap(view)) {

			private static final long serialVersionUID = -2919912669513267746L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				callback.createModalWindow(target, InitiativeOrderWindow::new,
					getModelObject());
			}
		});

		add(new AjaxLink<>("next", ModelMaker.wrap(view)) {
			private static final long serialVersionUID = 1349414335138202154L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				initiativeService.selectNext(getModelObject());
				callback.refreshMenus(target);
				current.detach();
				target.add(current);
			}

			public boolean isVisible() {
				return super.isVisible() && !UNKNOWN.equals(current.getDefaultModelObject());
			}

		});

		add(new ListView<>("conditions", createConditionsModel()) {
			private static final long serialVersionUID = 52404040479457513L;

			@Override
			protected void populateItem(ListItem<InitiativeParticipantCondition> item) {
				InitiativeParticipantCondition condition = item.getModelObject();
				Integer turnsRemaining = condition.getTurnsRemaining();

				AjaxLink<InitiativeParticipantCondition> link = new AjaxLink<InitiativeParticipantCondition>(
					"condition") {
					private static final long serialVersionUID = 4773340861141521781L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						callback.createModalWindow(target,
							InitiativeParticipantConditionEditWindow::new, item.getModelObject());

					}
				};

				link.add(new Label("description", condition.getDescription()));
				link.add(new Label("count", turnsRemaining)
					.setVisible(turnsRemaining != null));

				item.add(link);
			}
		});

		IModel<InitiativeParticipant> conditionModel = createConditionModel();

		add(new AjaxLink<>("condition", conditionModel) {
			private static final long serialVersionUID = -290823527226125069L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				callback.createModalWindow(target,
					InitiativeParticipantConditionCreateWindow::new,
					currentParticipantModel.getObject());
			}

			@Override
			protected void onConfigure() {
				super.onConfigure();

				setVisible(getModelObject() != null);
			}
		});
	}

	private IModel<InitiativeParticipant> createConditionModel() {
		return new LoadableDetachableModel<>() {
			private static final long serialVersionUID = 8258325180493805150L;

			@Override
			protected InitiativeParticipant load() {
				return initiativeDAO.getUniqueByFilter(new InitiativeParticipantFilter().view(getModelObject())
					.selected(true)).getOrNull();
			}
		};
	}


	private IModel<? extends List<InitiativeParticipantCondition>> createConditionsModel() {
		return currentParticipantModel.map(p -> {
			InitiativeParticipantConditionFilter filter = new InitiativeParticipantConditionFilter();
			filter.participant(p);
			filter.description().orderBy(true);

			return conditionDAO.findByFilter(filter).toJavaList();
		}).orElseGet(List::of);
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		currentParticipantModel.detach();
	}
}
