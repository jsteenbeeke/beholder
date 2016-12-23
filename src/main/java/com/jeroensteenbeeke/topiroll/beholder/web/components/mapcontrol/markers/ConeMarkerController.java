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
import com.jeroensteenbeeke.topiroll.beholder.entities.ConeMarker;

public class ConeMarkerController extends Panel {

	private static final long serialVersionUID = 1L;

	private IByFunctionModel<ConeMarker> markerModel;

	@Inject
	private MarkerService markerService;

	private TextField<String> colorField;
	
	private NumberTextField<Integer> offsetXField;

	private NumberTextField<Integer> offsetYField;

	private NumberTextField<Integer> radiusField;
	
	private NumberTextField<Integer> thetaField;

	public ConeMarkerController(String id, ConeMarker marker) {
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
		
		thetaField = new NumberTextField<>("theta",
				Model.of(marker.getTheta()));
		thetaField.setMinimum(0);
		thetaField.setMaximum(359);

		colorField.add(new ConeMarkerUpdateBehavior());
		offsetXField.add(new ConeMarkerUpdateBehavior());
		offsetYField.add(new ConeMarkerUpdateBehavior());
		radiusField.add(new ConeMarkerUpdateBehavior());
		thetaField.add(new ConeMarkerUpdateBehavior());

		add(colorField);
		add(offsetXField);
		add(offsetYField);
		add(radiusField);
		add(thetaField);

	}

	public class ConeMarkerUpdateBehavior
			extends AjaxFormComponentUpdatingBehavior {
		private static final long serialVersionUID = 1L;

		public ConeMarkerUpdateBehavior() {
			super("change");
		}

		@Override
		protected void onUpdate(AjaxRequestTarget target) {
			final String color = colorField.getModelObject();
			final int x = offsetXField.getModelObject();
			final int y = offsetYField.getModelObject();
			final int radius = radiusField.getModelObject();
			final int theta = thetaField.getModelObject();

			markerService.update(markerModel.getObject(), color, x, y, radius, theta);

		}
	}
}
