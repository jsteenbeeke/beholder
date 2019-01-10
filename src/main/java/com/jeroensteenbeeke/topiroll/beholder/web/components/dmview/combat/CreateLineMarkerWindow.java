package com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.combat;

import com.jeroensteenbeeke.hyperion.heinlein.web.components.ButtonType;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MarkerService;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;
import com.jeroensteenbeeke.topiroll.beholder.util.Calculations;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMModalWindow;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewCallback;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.PatternValidator;

import javax.inject.Inject;
import java.awt.*;
import java.util.Optional;

public class CreateLineMarkerWindow extends DMModalWindow<MapView> {

	private static final long serialVersionUID = 3458840318488583468L;

	public CreateLineMarkerWindow(String id, MapView view, DMViewCallback callback) {
		super(id, ModelMaker.wrap(view), "Create Line Marker");

		Point location = callback.getPreviousClickedLocation();
		final int x = location.x;
		final int y = location.y;

		Point directionalLocation = callback.getClickedLocation();
		final int tx = directionalLocation.x;
		final int ty = directionalLocation.y;

		int theta = Calculations.getTheta(x, y, tx, ty);

		int dx = tx - x;
		int dy = ty - y;

		double factor = Optional.ofNullable(view.getSelectedMap()).map(m -> m.getDisplayFactor(view))
				.orElse(1.0);

		int ext = Math.max(1, (int) (Math.sqrt(dx*dx + dy * dy) / factor));

		NumberTextField<Integer> extentField = new NumberTextField<>("extent", Model.of(ext));
		extentField.setMinimum(1);
		extentField.setRequired(true);

		NumberTextField<Integer> angleField = new NumberTextField<>("angle", Model.of(theta));
		angleField.setMinimum(0);
		angleField.setMaximum(359);
		angleField.setRequired(true);

		TextField<String> colorField = new TextField<>("color", Model.of("ff0000"));
		colorField.setRequired(true);
		colorField.add(new PatternValidator("^[0-9a-fA-F]{6}$"));

		Form<TokenInstance> damageForm = new Form<TokenInstance>("form") {
			private static final long serialVersionUID = 8331302186400541988L;
			@Inject
			private MarkerService markerService;

			@Override
			protected void onSubmit() {
				MapView view = CreateLineMarkerWindow.this.getModelObject();

				markerService.createLine(view, colorField.getModelObject(), x, y, extentField.getModelObject(), angleField.getModelObject());
			}
		};

		damageForm.add(extentField);
		damageForm.add(angleField);
		damageForm.add(colorField);

		add(damageForm);

		addAjaxSubmitButton(target -> {
			setVisible(false);

			target.add(CreateLineMarkerWindow.this);
			target.appendJavaScript("$('#combat-modal').modal('hide');");

			callback.redrawMap(target);
			callback.removeModal(target);
		}).forForm(damageForm).ofType(ButtonType.Primary).withLabel("Save changes");
	}
}
