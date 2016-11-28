package com.jeroensteenbeeke.topiroll.beholder.web.components;

import javax.inject.Inject;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.UrlUtils;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.util.time.Duration;

import com.jeroensteenbeeke.topiroll.beholder.beans.MapRenderers;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.util.AjaxRequestTargetJavaScriptHandler;
import com.jeroensteenbeeke.topiroll.beholder.util.OnDomReadyJavaScriptHandler;

public class MapCanvas extends WebComponent {
	private static final long serialVersionUID = 1L;

	@Inject
	private MapRenderers renderers;

	private IModel<MapView> viewModel;

	private final boolean previewMode;


	public MapCanvas(String id, IModel<MapView> viewModel,
			boolean previewMode) {
		super(id);
		setOutputMarkupId(true);
		this.viewModel = viewModel;
		this.previewMode = previewMode;
		
		add(AttributeModifier.replace("style",
				new LoadableDetachableModel<String>() {
					private static final long serialVersionUID = 1L;

					@Override
					protected String load() {
						return String.format("background-image: url('%s');", UrlUtils.rewriteToContextRelative("img/fog-of-war.png",
								getRequestCycle()));
					}
				}));

		// Hack
		add(AttributeModifier.replace("id",
				new LoadableDetachableModel<String>() {
					private static final long serialVersionUID = 1L;

					@Override
					protected String load() {
						return getMarkupId();
					}
				}));
	}

	@Override
	protected void onRender() {
		super.onRender();
	}

	@Override
	protected void onInitialize() {

		super.onInitialize();

		add(new AbstractAjaxTimerBehavior(Duration.seconds(5)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onTimer(AjaxRequestTarget target) {
				final String canvasId = MapCanvas.this.getMarkupId();

				MapView view = viewModel.getObject();

				if (target != null) {
					AjaxRequestTargetJavaScriptHandler handler = new AjaxRequestTargetJavaScriptHandler(
							target);

					renderers.getRenderers().forEach(r -> {
						r.onRefresh(canvasId, handler, view, previewMode);
					});
				}
				
				
			}
		});
	}

	@Override
	protected void onComponentTag(ComponentTag tag) {
		checkComponentTag(tag, "canvas");

	}

	@Override
	protected void onDetach() {
		super.onDetach();

		viewModel.detach();
	}

	@Override
	public void renderHead(IHeaderResponse response) {

		super.renderHead(response);

		response.render(JavaScriptHeaderItem
				.forReference(new JavaScriptResourceReference(MapCanvas.class,
						"js/renderstate.js")));

		OnDomReadyJavaScriptHandler handler = new OnDomReadyJavaScriptHandler(
				response);

		renderers.getRenderers().forEach(r -> {
			r.onRefresh(getMarkupId(), handler, viewModel.getObject(), previewMode);
		});
	}
}
