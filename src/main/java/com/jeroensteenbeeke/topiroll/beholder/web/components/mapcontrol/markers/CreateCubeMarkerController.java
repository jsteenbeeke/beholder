package com.jeroensteenbeeke.topiroll.beholder.web.components.mapcontrol.markers;

import com.jeroensteenbeeke.hyperion.ducktape.web.components.TypedPanel;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MarkerService;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.web.components.mapcontrol.MarkerController;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.PatternValidator;

import javax.inject.Inject;

public abstract class CreateCubeMarkerController extends TypedPanel<MapView> {

	private static final long serialVersionUID = 1L;

	@Inject
	private MarkerService markerService;

	public CreateCubeMarkerController(String id, MapView view, int x, int y) {
		super(id, ModelMaker.wrap(view));

		Form<MapView> form = new Form<>("createForm");

		WebMarkupContainer preview = new WebMarkupContainer("preview");

		TextField<String> colorField =
				new TextField<>("color", Model.of("ff0000"));
		colorField.add(new PatternValidator("[0-9a-fA-F]{6}"));
		colorField.add(new AjaxFormComponentUpdatingBehavior("change") {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(preview);
			}
		});

		preview.setOutputMarkupId(true);
		preview.add(AttributeModifier.replace("style", new LoadableDetachableModel<String>() {
			@Override
			protected String load() {
				return String.format("background-color: #%s !important;", colorField
						.getModelObject());
			}
		}));

		NumberTextField<Integer> radiusField =
				new NumberTextField<>("r", Model.of(5));
		radiusField.setMinimum(1);

		form.add(colorField);
		form.add(radiusField);
		form.add(preview);

		form.add(new AjaxSubmitLink("submit", form) {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				super.onSubmit(target, form);

				markerService.createCube(
						CreateCubeMarkerController.this.getModelObject(),
						colorField.getModelObject(), x, y,
						radiusField.getModelObject());

				replaceMe(target, new MarkerController(id,
						CreateCubeMarkerController.this.getModelObject()) {
					@Override
					public void replaceMe(AjaxRequestTarget target,
										  WebMarkupContainer replacement) {
						CreateCubeMarkerController.this.replaceMe(target, replacement);
					}
				});
			}
		});

		add(form);
	}

	public abstract void replaceMe(AjaxRequestTarget target,
								   WebMarkupContainer replacement);
}

