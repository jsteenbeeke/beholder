/**
 * This file is part of Beholder
 * (C) 2016 Jeroen Steenbeeke
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

package com.jeroensteenbeeke.topiroll.beholder.web.components.mapcontrol.markers;

import com.jeroensteenbeeke.hyperion.solstice.data.IByFunctionModel;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MarkerService;
import com.jeroensteenbeeke.topiroll.beholder.entities.CubeMarker;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.PatternValidator;

import javax.inject.Inject;

public class CubeMarkerController extends Panel {

	private static final long serialVersionUID = 1L;

	private IByFunctionModel<CubeMarker> markerModel;

	@Inject
	private MarkerService markerService;
	
	private TextField<String> colorField;

	private NumberTextField<Integer> offsetXField;

	private NumberTextField<Integer> offsetYField;

	private NumberTextField<Integer> radiusField;

	public CubeMarkerController(String id, CubeMarker marker) {
		super(id);

		this.markerModel = ModelMaker.wrap(marker);
		
		colorField = new TextField<>("color", Model.of(marker.getColor()));
		colorField.add(new PatternValidator("[0-9a-fA-F]{6}"));

		offsetXField = new NumberTextField<>("x",
				Model.of(marker.getOffsetX()));
		offsetXField.setMinimum(0);
		offsetXField.setStep(new SquareStepModel(markerModel));

		offsetYField = new NumberTextField<>("y",
				Model.of(marker.getOffsetY()));
		offsetYField.setMinimum(0);
		offsetYField.setStep(new SquareStepModel(markerModel));

		radiusField = new NumberTextField<>("r",
				Model.of(marker.getExtent()));
		radiusField.setMinimum(1);

		colorField.add(new CubeMarkerUpdateBehavior());
		offsetXField.add(new CubeMarkerUpdateBehavior());
		offsetYField.add(new CubeMarkerUpdateBehavior());
		radiusField.add(new CubeMarkerUpdateBehavior());

		add(colorField);
		add(offsetXField);
		add(offsetYField);
		add(radiusField);

	}

	@Override
	protected void onDetach() {
		super.onDetach();
		markerModel.detach();
	}

	public class CubeMarkerUpdateBehavior
			extends AjaxFormComponentUpdatingBehavior {
		private static final long serialVersionUID = 1L;

		public CubeMarkerUpdateBehavior() {
			super("change");
		}

		@Override
		protected void onUpdate(AjaxRequestTarget target) {
			final String color = colorField.getModelObject();
			final int x = offsetXField.getModelObject();
			final int y = offsetYField.getModelObject();
			final int radius = radiusField.getModelObject();

			markerService.update(markerModel.getObject(), color, x, y, radius);

		}
	}
}
