package com.jeroensteenbeeke.topiroll.beholder.web.components.mapcontrol.markers;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.PatternValidator;

import com.jeroensteenbeeke.hyperion.solstice.data.IByFunctionModel;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MarkerService;
import com.jeroensteenbeeke.topiroll.beholder.entities.CircleMarker;

public class CircleMarkerController extends Panel {

	

	private static final long serialVersionUID = 1L;

	private IByFunctionModel<CircleMarker> markerModel;

	@Inject
	private MarkerService markerService;

	private TextField<String> colorField;
	
	private NumberTextField<Integer> offsetXField;

	private NumberTextField<Integer> offsetYField;

	private NumberTextField<Integer> radiusField;

	public CircleMarkerController(String id, CircleMarker marker) {
		super(id);

		this.markerModel = ModelMaker.wrap(marker);

		colorField = new TextField<String>("color", Model.of(marker.getColor()));
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
		
		colorField.add(new CircleMarkerUpdateBehavior());
		offsetXField.add(new CircleMarkerUpdateBehavior());
		offsetYField.add(new CircleMarkerUpdateBehavior());
		radiusField.add(new CircleMarkerUpdateBehavior());

		add(colorField);
		add(offsetXField);
		add(offsetYField);
		add(radiusField);

	}
	
	

	public class CircleMarkerUpdateBehavior
			extends AjaxFormComponentUpdatingBehavior {
		private static final long serialVersionUID = 1L;

		public CircleMarkerUpdateBehavior() {
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
