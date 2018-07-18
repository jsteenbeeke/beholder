package com.jeroensteenbeeke.topiroll.beholder.web.components.combat;

import com.jeroensteenbeeke.hyperion.solstice.data.IByFunctionModel;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeParticipant;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.LoadableDetachableModel;

import javax.inject.Inject;
import java.awt.*;
import java.util.Optional;

public class MapOptionsPanel extends CombatModePanel<MapView> {
	public MapOptionsPanel(String id, MapView view, CombatModeCallback callback) {
		super(id);

		IByFunctionModel<MapView> viewModel = ModelMaker.wrap(view);
		setModel(viewModel);

		add(new Label("location", new LoadableDetachableModel<String>() {
			@Override
			protected String load() {
				return Optional.ofNullable(callback.getClickedLocation()).map(p -> String.format
						("(%d, %d)", p.x, p.y)).orElse("-");
			}
		}));

		add(new AjaxLink<InitiativeParticipant>("gather") {
			@Inject
			private MapService mapService;

			@Override
			public void onClick(AjaxRequestTarget target) {
				MapView view = MapOptionsPanel.this.getModelObject();

				Point p = callback.getClickedLocation();

				mapService.gatherPlayerTokens(view, p.x, p.y);

				callback.redrawMap(target);
			}
		});

		add(new AjaxLink<MapView>("newcirclemarker") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				callback.createModalWindow(target, CreateCircleMarkerPanel::new, MapOptionsPanel.this.getModelObject());
			}
		});

		add(new AjaxLink<InitiativeParticipant>("newcubemarker") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				callback.createModalWindow(target, CreateCubeMarkerPanel::new, MapOptionsPanel.this.getModelObject());

			}

		});

		add(new AjaxLink<InitiativeParticipant>("newraymarker") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				callback.createModalWindow(target, CreateLineMarkerPanel::new, MapOptionsPanel.this.getModelObject());

			}

			@Override
			public boolean isVisible() {
				return super.isVisible() && callback.getPreviousClickedLocation() != null;
			}
		});

		add(new AjaxLink<InitiativeParticipant>("newconemarker") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				callback.createModalWindow(target, CreateConeMarkerPanel::new, MapOptionsPanel.this.getModelObject());
			}

			@Override
			public boolean isVisible() {
				return super.isVisible() && callback.getPreviousClickedLocation() != null;
			}

		});

		add(new AjaxLink<ScaledMap>("newtoken", viewModel.getProperty(MapView::getSelectedMap)) {
			@Override
			public void onClick(AjaxRequestTarget target) {
				callback.createModalWindow(target, CreateTokenPanel::new, getModelObject());
			}
		});


	}
}
