package com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.combat;

import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.AreaMarkerDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewCallback;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;

import javax.inject.Inject;

public class MarkerStatusPanel extends DMViewPanel<MapView> {
	@Inject
	private MapService mapService;

	public MarkerStatusPanel(String id, DMViewCallback callback) {
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
