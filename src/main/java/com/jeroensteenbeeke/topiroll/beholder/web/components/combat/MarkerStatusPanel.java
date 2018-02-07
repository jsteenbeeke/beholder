package com.jeroensteenbeeke.topiroll.beholder.web.components.combat;

import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.beans.MarkerService;
import com.jeroensteenbeeke.topiroll.beholder.dao.AreaMarkerDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenBorderType;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.LoadableDetachableModel;

import javax.inject.Inject;
import java.util.Optional;

public class MarkerStatusPanel extends CombatModePanel<MapView> {
	@Inject
	private MapService mapService;

	public MarkerStatusPanel(String id, CombatModeCallback callback) {
		super(id);

		add(new AjaxLink<TokenInstance>("remove") {
			@Inject
			private AreaMarkerDAO markerDAO;

			@Override
			public void onClick(AjaxRequestTarget target) {
				markerDAO.delete(callback.getSelectedMarker());

				callback.redrawMap(target);

				MarkerStatusPanel.this.setVisible(false);
				target.add(MarkerStatusPanel.this);
			}
		});

	}

}
