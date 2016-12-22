package com.jeroensteenbeeke.topiroll.beholder.web.components.mapcontrol.markers;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.validation.validator.PatternValidator;

import com.jeroensteenbeeke.hyperion.solstice.data.IByFunctionModel;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MarkerService;
import com.jeroensteenbeeke.topiroll.beholder.entities.LineMarker;

public class LineMarkerController extends Panel {

	private static final long serialVersionUID = 1L;

	private IByFunctionModel<LineMarker> markerModel;

	@Inject
	private MarkerService markerService;

	private TextField<String> colorField;

	private NumberTextField<Integer> offsetXField;

	private NumberTextField<Integer> offsetYField;

	private NumberTextField<Integer> radiusField;

	private NumberTextField<Integer> thetaField;

	public LineMarkerController(String id, LineMarker marker) {
		super(id);

		this.markerModel = ModelMaker.wrap(marker);

		colorField = new TextField<>("color",
				markerModel.getProperty(LineMarker::getColor));
		colorField.add(new PatternValidator("[0-9a-fA-F]{6}"));

		offsetXField = new NumberTextField<>("x",
				markerModel.getProperty(LineMarker::getOffsetX));
		offsetXField.setMinimum(0);

		offsetYField = new NumberTextField<>("y",
				markerModel.getProperty(LineMarker::getOffsetY));
		offsetYField.setMinimum(0);

		radiusField = new NumberTextField<>("r",
				markerModel.getProperty(LineMarker::getExtent));
		radiusField.setMinimum(1);

		thetaField = new NumberTextField<>("theta",
				markerModel.getProperty(LineMarker::getTheta));
		thetaField.setMinimum(0);
		thetaField.setMaximum(359);

		colorField.add(new LineMarkerUpdateBehavior());
		offsetXField.add(new LineMarkerUpdateBehavior());
		offsetYField.add(new LineMarkerUpdateBehavior());
		radiusField.add(new LineMarkerUpdateBehavior());
		thetaField.add(new LineMarkerUpdateBehavior());

		add(colorField);
		add(offsetXField);
		add(offsetYField);
		add(radiusField);
		add(thetaField);

	}

	public class LineMarkerUpdateBehavior
			extends AjaxFormComponentUpdatingBehavior {
		private static final long serialVersionUID = 1L;

		public LineMarkerUpdateBehavior() {
			super("blur");
		}

		@Override
		protected void onUpdate(AjaxRequestTarget target) {
			final String color = colorField.getModelObject();
			final int x = offsetXField.getModelObject();
			final int y = offsetYField.getModelObject();
			final int radius = radiusField.getModelObject();
			final int theta = thetaField.getModelObject();

			markerService.update(markerModel.getObject(), color, x, y, radius,
					theta);

		}
	}
}
