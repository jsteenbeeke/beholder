package com.jeroensteenbeeke.topiroll.beholder.web.components.combat;

import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MarkerService;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;
import com.jeroensteenbeeke.topiroll.beholder.util.Calculations;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.PatternValidator;

import javax.inject.Inject;
import java.awt.*;

public class CreateConeMarkerPanel extends CombatModePanel<MapView> {

	public CreateConeMarkerPanel(String id, MapView view, CombatModeCallback callback) {
		super(id, ModelMaker.wrap(view));

		Point location = callback.getPreviousClickedLocation();
		final int x = location.x;
		final int y = location.y;

		Point directionalLocation = callback.getClickedLocation();
		final int tx = directionalLocation.x;
		final int ty = directionalLocation.y;

		int theta = Calculations.getTheta(x, y, tx, ty);

		NumberTextField<Integer> radiusField = new NumberTextField<>("radius", Model.of(5));
		radiusField.setMinimum(1);
		radiusField.setRequired(true);

		NumberTextField<Integer> angleField = new NumberTextField<>("angle", Model.of(theta));
		angleField.setMinimum(0);
		angleField.setMaximum(359);
		angleField.setRequired(true);

		TextField<String> colorField = new TextField<>("color", Model.of("ff0000"));
		colorField.setRequired(true);
		colorField.add(new PatternValidator("^[0-9a-fA-F]{6}$"));

		Form<TokenInstance> damageForm = new Form<TokenInstance>("form") {
			@Inject
			private MarkerService markerService;

			@Override
			protected void onSubmit() {
				MapView view = CreateConeMarkerPanel.this.getModelObject();

				ScaledMap map = view.getSelectedMap();
				Integer radius = radiusField.getModelObject();
				if (map != null) {
					double adjust = (radius * map.getSquareSize()) / 5.0;

					markerService.createCone(view, colorField.getModelObject(), (int) (x - adjust), (int) (y - adjust), radius, angleField.getModelObject());
				} else {
					markerService.createCone(view, colorField.getModelObject(), x, y, radius, angleField.getModelObject());
				}
			}
		};

		damageForm.add(radiusField);
		damageForm.add(angleField);
		damageForm.add(colorField);

		add(damageForm);

		add(new AjaxSubmitLink("submit", damageForm) {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				super.onSubmit(target, form);

				setVisible(false);

				target.add(CreateConeMarkerPanel.this);
				target.appendJavaScript("$('#combat-modal').modal('hide');");

				callback.redrawMap(target);
				callback.removeModal(target);
			}
		});
	}
}
