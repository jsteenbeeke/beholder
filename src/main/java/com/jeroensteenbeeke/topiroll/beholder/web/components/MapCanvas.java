package com.jeroensteenbeeke.topiroll.beholder.web.components;

import javax.inject.Inject;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
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

	private long lastVersion;
	
	public MapCanvas(String id, IModel<MapView> viewModel, boolean previewMode) {
		super(id);
		this.viewModel = viewModel;
		this.lastVersion = viewModel.getObject().getVersion();
		
		add(AttributeModifier.replace("width", new LoadableDetachableModel<String>() {
			@Override
			protected String load() {
				int width = viewModel.getObject().getWidth();
				
				if (previewMode) {
					width = width / 4;
				}
				
				return Integer.toString(width);
			}
		}));
		add(AttributeModifier.replace("height", new LoadableDetachableModel<String>() {
			@Override
			protected String load() {
				int height = viewModel.getObject().getHeight();
				
				if (previewMode) {
					height = height /4;
				}
				
				return Integer.toString(height);
			}
		}));
		
		add(new AbstractAjaxTimerBehavior(Duration.seconds(1)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onTimer(AjaxRequestTarget target) {
				final String canvasId = MapCanvas.this.getMarkupId();

				MapView view = viewModel.getObject();

				if (view.getVersion() > lastVersion) {
					lastVersion = view.getVersion();

					if (target != null) {
						AjaxRequestTargetJavaScriptHandler handler = new AjaxRequestTargetJavaScriptHandler(
								target);
						
						renderers.getRenderers().forEach(r -> {
							r.onRefresh(canvasId, handler, view);
						});
					}
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
		
		OnDomReadyJavaScriptHandler handler = new OnDomReadyJavaScriptHandler(response);
		
		renderers.getRenderers().forEach(r -> {
			r.onRefresh(getMarkupId(), handler, viewModel.getObject());
		});
	}
}
