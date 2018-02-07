package com.jeroensteenbeeke.topiroll.beholder.web.components.combat;

import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MarkerService;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.PatternValidator;

import javax.inject.Inject;
import java.awt.*;
import java.util.Optional;

public class CreateCircleMarkerPanel extends CombatModePanel<MapView> {

	public CreateCircleMarkerPanel(String id, MapView view, CombatModeCallback callback) {
		super(id, ModelMaker.wrap(view));

		Point location = callback.getClickedLocation();
		final int x = location.x;
		final int y = location.y;

		NumberTextField<Integer> radiusField = new NumberTextField<>("radius", Model.of(5));
		radiusField.setMinimum(1);
		radiusField.setRequired(true);

		TextField<String> colorField = new TextField<>("color", Model.of("ff0000"));
		colorField.setRequired(true);
		colorField.add(new PatternValidator("^[0-9a-fA-F]{6}$"));

		Form<TokenInstance> damageForm = new Form<TokenInstance>("form") {
			@Inject
			private MarkerService markerService;

			@Override
			protected void onSubmit() {
				MapView view = CreateCircleMarkerPanel.this.getModelObject();

				ScaledMap map = view.getSelectedMap();
				Integer radius = radiusField.getModelObject();
				if (map != null) {
					double adjust = (radius * map.getSquareSize()) / 5.0;

					markerService.createCircle(view, colorField.getModelObject(), (int) (x - adjust), (int) (y - adjust), radius);
				} else {
					markerService.createCircle(view, colorField.getModelObject(), x, y, radius);
				}
			}
		};

		damageForm.add(radiusField);
		damageForm.add(colorField);

		add(damageForm);

		add(new AjaxSubmitLink("submit", damageForm) {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				super.onSubmit(target, form);

				setVisible(false);

				target.add(CreateCircleMarkerPanel.this);
				target.appendJavaScript("$('#combat-modal').modal('hide');");

				callback.redrawMap(target);
				callback.removeModal(target);
			}
		});
	}
}
