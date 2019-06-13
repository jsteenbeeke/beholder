/**
 * This file is part of Beholder
 * (C) 2016-2019 Jeroen Steenbeeke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.combat;

import com.jeroensteenbeeke.hyperion.heinlein.web.components.ButtonType;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MarkerService;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
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

public class CreateConeMarkerWindow extends DMModalWindow<MapView> {

	public CreateConeMarkerWindow(String id, MapView view, DMViewCallback callback) {
		super(id, ModelMaker.wrap(view), "Create Cone Marker");

		Point location = callback.getPreviousClickedLocation().orElseThrow(CannotCreateModalWindowException::new);
		final int x = location.x;
		final int y = location.y;

		Point directionalLocation = callback.getClickedLocation().orElseThrow(CannotCreateModalWindowException::new);
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

		Form<TokenInstance> markerForm = new Form<TokenInstance>("form") {
			private static final long serialVersionUID = 841367108470725073L;
			@Inject
			private MarkerService markerService;

			@Override
			protected void onSubmit() {
				MapView view = CreateConeMarkerWindow.this.getModelObject();

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

		markerForm.add(radiusField);
		markerForm.add(angleField);
		markerForm.add(colorField);

		add(markerForm);

		addAjaxSubmitButton(target -> {
			setVisible(false);

			target.add(CreateConeMarkerWindow.this);
			target.appendJavaScript("$('#combat-modal').modal('hide');");

			callback.redrawMap(target);
			callback.removeModal(target);
		}).forForm(markerForm).ofType(ButtonType.Primary).withLabel("Save changes");
	}
}
