package com.jeroensteenbeeke.topiroll.beholder.web.components.mapcontrol.markers;

import com.jeroensteenbeeke.hyperion.webcomponents.core.TypedPanel;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;

public abstract class SelectMarkerTypeController extends TypedPanel<MapView> {
	public SelectMarkerTypeController(String id, MapView view, int x, int y) {
		super(id, ModelMaker.wrap(view));

		add(new AjaxLink<Void>("circle") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				replaceMe(target, new CreateCircleMarkerController(id, view, x, y) {
					@Override
					public void replaceMe(AjaxRequestTarget target,
							WebMarkupContainer replacement) {
						SelectMarkerTypeController.this.replaceMe(target, replacement);
					}
				});
			}
		});
		add(new AjaxLink<Void>("cone") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				replaceMe(target, new CreateConeMarkerController(id, view, x, y) {
					@Override
					public void replaceMe(AjaxRequestTarget target,
							WebMarkupContainer replacement) {
						SelectMarkerTypeController.this.replaceMe(target, replacement);
					}
				});
			}
		});
		add(new AjaxLink<Void>("cube") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				replaceMe(target, new CreateCubeMarkerController(id, view, x, y) {
					@Override
					public void replaceMe(AjaxRequestTarget target,
							WebMarkupContainer replacement) {
						SelectMarkerTypeController.this.replaceMe(target, replacement);
					}
				});
			}
		});
		add(new AjaxLink<Void>("line") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				replaceMe(target, new CreateLineMarkerController(id, view, x, y) {
					@Override
					public void replaceMe(AjaxRequestTarget target,
							WebMarkupContainer replacement) {
						SelectMarkerTypeController.this.replaceMe(target, replacement);
					}
				});
			}
		});

	}

	public abstract void replaceMe(AjaxRequestTarget target,
			WebMarkupContainer replacement);

}
