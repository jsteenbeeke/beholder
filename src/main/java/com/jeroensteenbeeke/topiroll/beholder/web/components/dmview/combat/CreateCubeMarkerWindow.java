package com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.combat;

import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MarkerService;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;
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

public class CreateCubeMarkerWindow extends DMViewPanel<MapView> {


	public CreateCubeMarkerWindow(String id, MapView view, DMViewCallback callback) {
		super(id, ModelMaker.wrap(view));

		Point location = callback.getClickedLocation();
		final int x = location.x;
		final int y = location.y;

		NumberTextField<Integer> extentField = new NumberTextField<>("extent", Model.of(5));
		extentField.setMinimum(1);
		extentField.setRequired(true);

		TextField<String> colorField = new TextField<>("color", Model.of("ff0000"));
		colorField.setRequired(true);
		colorField.add(new PatternValidator("^[0-9a-fA-F]{6}$"));

		Form<TokenInstance> damageForm = new Form<TokenInstance>("form") {
			@Inject
			private MarkerService markerService;

			@Override
			protected void onSubmit() {
				MapView view = CreateCubeMarkerWindow.this.getModelObject();

				ScaledMap map = view.getSelectedMap();
				Integer radius = extentField.getModelObject();
				if (map != null) {
					double adjust = (radius * map.getSquareSize()) / 5.0;

					markerService.createCube(view, colorField.getModelObject(), (int) (x - adjust), (int) (y - adjust), radius);
				} else {
					markerService.createCube(view, colorField.getModelObject(), x, y, radius);
				}
			}
		};

		damageForm.add(extentField);
		damageForm.add(colorField);

		add(damageForm);

		add(new AjaxSubmitLink("submit", damageForm) {
			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				super.onSubmit(target);

				setVisible(false);

				target.add(CreateCubeMarkerWindow.this);
				target.appendJavaScript("$('#combat-modal').modal('hide');");

				callback.redrawMap(target);
				callback.removeModal(target);
			}
		});
	}
}
