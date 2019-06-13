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

public class CreateCubeMarkerWindow extends DMModalWindow<MapView> {
	private static final long serialVersionUID = -3030894520335324792L;

	public CreateCubeMarkerWindow(String id, MapView view, DMViewCallback callback) {
		super(id, ModelMaker.wrap(view), "Create Cube Marker");

		Point location = callback.getClickedLocation().orElseThrow(CannotCreateModalWindowException::new);;
		final int x = location.x;
		final int y = location.y;

		NumberTextField<Integer> extentField = new NumberTextField<>("extent", Model.of(5));
		extentField.setMinimum(1);
		extentField.setRequired(true);

		TextField<String> colorField = new TextField<>("color", Model.of("ff0000"));
		colorField.setRequired(true);
		colorField.add(new PatternValidator("^[0-9a-fA-F]{6}$"));

		Form<TokenInstance> damageForm = new Form<TokenInstance>("form") {
			private static final long serialVersionUID = -5783981070891861232L;
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

		addAjaxSubmitButton(target -> {
			setVisible(false);

			target.add(CreateCubeMarkerWindow.this);
			target.appendJavaScript("$('#combat-modal').modal('hide');");

			callback.redrawMap(target);
			callback.removeModal(target);
		}).forForm(damageForm).ofType(ButtonType.Primary).withLabel("Save changes");
	}
}
