package com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.combat;

import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.InitiativeService;
import com.jeroensteenbeeke.topiroll.beholder.dao.InitiativeParticipantDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeParticipant;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.InitiativeParticipantFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewCallback;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.LoadableDetachableModel;

import javax.inject.Inject;

public class InitiativePanel extends DMViewPanel<MapView> {
	private static final String UNKNOWN = "-";
	@Inject
	private InitiativeParticipantDAO initiativeDAO;

	@Inject
	private InitiativeService initiativeService;

	public InitiativePanel(String id, MapView view, DMViewCallback callback) {
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
				return initiativeDAO.getUniqueByFilter(new InitiativeParticipantFilter()
						.view(getModelObject())
						.selected(true))
						.map(InitiativeParticipant::getName)
						.getOrElse(UNKNOWN);
			}
		}) {
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
				target.add(InitiativePanel.this);
			}
		});

	}
}
