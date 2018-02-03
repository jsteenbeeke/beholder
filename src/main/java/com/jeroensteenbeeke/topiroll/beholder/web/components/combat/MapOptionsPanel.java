package com.jeroensteenbeeke.topiroll.beholder.web.components.combat;

import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeParticipant;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;

import java.awt.*;

public class MapOptionsPanel extends CombatModePanel<MapView> {
	public MapOptionsPanel(String id, MapView view, CombatModeCallback callback) {
		super(id, ModelMaker.wrap(view));

		add(new AjaxLink<InitiativeParticipant>("gather") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				MapView view = MapOptionsPanel.this.getModelObject();

				Point p = callback.getClickedLocation();
			}
		});

		add(new AjaxLink<InitiativeParticipant>("newmarker") {
			@Override
			public void onClick(AjaxRequestTarget target) {
			}
		});

		add(new AjaxLink<InitiativeParticipant>("newtoken") {
			@Override
			public void onClick(AjaxRequestTarget target) {
			}
		});
	}
}
