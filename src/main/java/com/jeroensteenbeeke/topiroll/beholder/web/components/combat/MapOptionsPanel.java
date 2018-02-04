package com.jeroensteenbeeke.topiroll.beholder.web.components.combat;

import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeParticipant;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;

import javax.inject.Inject;
import java.awt.*;
import java.util.Optional;

public class MapOptionsPanel extends CombatModePanel<MapView> {
	public MapOptionsPanel(String id, MapView view, CombatModeCallback callback) {
		super(id, ModelMaker.wrap(view));

		add(new AjaxLink<InitiativeParticipant>("gather") {
			@Inject
			private MapService mapService;

			@Override
			public void onClick(AjaxRequestTarget target) {
				MapView view = MapOptionsPanel.this.getModelObject();
				ScaledMap map = view.getSelectedMap();

				Point p = callback.getClickedLocation();

				int x = Optional.ofNullable(map)
						.map(m -> m.getDisplayFactor(view))
						.map(f -> p.x / f)
						.map(Math::round)
						.map(Long::intValue)
						.orElse(p.x);

				int y = Optional.ofNullable(map)
						.map(m -> m.getDisplayFactor(view))
						.map(f -> p.y / f)
						.map(Math::round)
						.map(Long::intValue)
						.orElse(p.y);

				mapService.gatherPlayerTokens(view, x, y);

				callback.redrawMap(target);
			}
		});

		add(new AjaxLink<InitiativeParticipant>("newmarker") {
			@Override
			public void onClick(AjaxRequestTarget target) {
			}
		});

		add(new AjaxLink<ScaledMap>("newtoken", ModelMaker.wrap(view.getSelectedMap())) {
			@Override
			public void onClick(AjaxRequestTarget target) {
				callback.createModalWindow(target, CreateTokenPanel::new,getModelObject());
			}
		});

	}
}
