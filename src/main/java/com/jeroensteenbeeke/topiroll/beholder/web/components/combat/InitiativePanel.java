package com.jeroensteenbeeke.topiroll.beholder.web.components.combat;

import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.InitiativeService;
import com.jeroensteenbeeke.topiroll.beholder.dao.InitiativeParticipantDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeParticipant;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.InitiativeParticipantFilter;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.LoadableDetachableModel;

import javax.inject.Inject;
import java.util.Optional;

public class InitiativePanel extends CombatModePanel<MapView> {
	private static final String UNKNOWN = "-";
	@Inject
	private InitiativeParticipantDAO initiativeDAO;

	@Inject
	private InitiativeService initiativeService;

	public InitiativePanel(String id, MapView view) {
		super(id, ModelMaker.wrap(view));

		setOutputMarkupId(true);

		add(new WebMarkupContainer("header") {
			@Override
			public boolean isVisible() {
				return super.isVisible() && UNKNOWN.equals(getDefaultModelObject());
			}
		});

		add(new Label("current", new LoadableDetachableModel<String>() {
			@Override
			protected String load() {
				return Optional.ofNullable(initiativeDAO.getUniqueByFilter(new InitiativeParticipantFilter()
						.view(getModelObject())
						.selected(true)))
						.map(InitiativeParticipant::getName)
						.orElse(UNKNOWN);
			}
		}) {
			@Override
			public boolean isVisible() {
				return super.isVisible() && !UNKNOWN.equals(getDefaultModelObject());
			}
		});

		add(new AjaxLink<MapView>("next", ModelMaker.wrap(view)) {
			@Override
			public void onClick(AjaxRequestTarget target) {
				initiativeService.selectNext(getModelObject());
				target.add(InitiativePanel.this);
			}
		});

	}
}
