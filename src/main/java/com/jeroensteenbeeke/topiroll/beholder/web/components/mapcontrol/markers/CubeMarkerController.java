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
import com.jeroensteenbeeke.topiroll.beholder.entities.CubeMarker;

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
		
		colorField = new TextField<>("color", markerModel.getProperty(CubeMarker::getColor));
		colorField.add(new PatternValidator("[0-9a-fA-F]{6}"));

		offsetXField = new NumberTextField<>("x",
				markerModel.getProperty(CubeMarker::getOffsetX));
		offsetXField.setMinimum(0);

		offsetYField = new NumberTextField<>("y",
				markerModel.getProperty(CubeMarker::getOffsetY));
		offsetYField.setMinimum(0);

		radiusField = new NumberTextField<>("r",
				markerModel.getProperty(CubeMarker::getExtent));
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

	public class CubeMarkerUpdateBehavior
			extends AjaxFormComponentUpdatingBehavior {
		private static final long serialVersionUID = 1L;

		public CubeMarkerUpdateBehavior() {
			super("blur");
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
