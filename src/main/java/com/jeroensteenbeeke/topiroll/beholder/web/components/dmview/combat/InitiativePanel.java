package com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.combat;

import com.google.common.collect.ImmutableList;
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
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import javax.inject.Inject;
import java.util.List;

public class InitiativePanel extends DMViewPanel<MapView> {
	private static final String UNKNOWN = "-";

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

		currentParticipantModel = new LoadableDetachableModel<InitiativeParticipant>() {
			@Override
			protected InitiativeParticipant load() {
				return initiativeDAO.getUniqueByFilter(new InitiativeParticipantFilter()
						.view(getModelObject())
						.selected(true)).getOrNull();
			}
		};

		add(current = new Label("current", currentParticipantModel.map(InitiativeParticipant::getName).orElse(UNKNOWN)) {
			@Override
			public boolean isVisible() {
				return super.isVisible() && !UNKNOWN.equals(getDefaultModelObject());
			}
		});

		add(new AjaxLink<MapView>("initiative", ModelMaker.wrap(view)) {

			@Override
			public void onClick(AjaxRequestTarget target) {
				callback.createModalWindow(target, InitiativeOrderWindow::new, getModelObject());
			}
		});

		add(new AjaxLink<MapView>("next", ModelMaker.wrap(view)) {
			@Override
			public void onClick(AjaxRequestTarget target) {
				initiativeService.selectNext(getModelObject());
				callback.refreshMenus(target);
			}

			public boolean isVisible() {
				return super.isVisible() && !UNKNOWN.equals(current.getDefaultModelObject());
			}

		});

		add(new ListView<InitiativeParticipantCondition>("conditions", createConditionsModel()) {
			@Override
			protected void populateItem(ListItem<InitiativeParticipantCondition> item) {
				InitiativeParticipantCondition condition = item.getModelObject();
				Integer turnsRemaining = condition.getTurnsRemaining();

				AjaxLink<InitiativeParticipantCondition> link = new AjaxLink<InitiativeParticipantCondition>("condition") {
					@Override
					public void onClick(AjaxRequestTarget target) {
						callback.createModalWindow(target, InitiativeParticipantConditionEditWindow::new, item.getModelObject());

					}
				};

				link.add(new Label("description", condition.getDescription()));
				link.add(new Label("count", turnsRemaining).setVisible(turnsRemaining != null));

				item.add(link);
			}
		});

		IModel<InitiativeParticipant> conditionModel = createConditionModel();

		add(new AjaxLink<InitiativeParticipant>("condition", conditionModel) {
			@Override
			public void onClick(AjaxRequestTarget target) {
				callback.createModalWindow(target, InitiativeParticipantConditionCreateWindow::new, currentParticipantModel.getObject());
			}

			@Override
			protected void onConfigure() {
				super.onConfigure();

				setVisible(getModelObject() != null);
			}
		});
	}

	private IModel<InitiativeParticipant> createConditionModel() {
		return new LoadableDetachableModel<InitiativeParticipant>() {
			@Override
			protected InitiativeParticipant load() {
				return initiativeDAO.getUniqueByFilter(new InitiativeParticipantFilter()
						.view(getModelObject())
						.selected(true))
						.getOrNull();
			}
		};
	}


	private IModel<? extends List<InitiativeParticipantCondition>> createConditionsModel() {
		return currentParticipantModel.map(p -> {
			InitiativeParticipantConditionFilter filter = new InitiativeParticipantConditionFilter();
			filter.participant(p);
			filter.description().orderBy(true);

			return conditionDAO.findByFilter(filter).toJavaList();
		}).orElseGet(ImmutableList::of);
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		currentParticipantModel.detach();
	}
}
