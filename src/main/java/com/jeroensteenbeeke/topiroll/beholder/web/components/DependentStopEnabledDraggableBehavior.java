package com.jeroensteenbeeke.topiroll.beholder.web.components;

import com.googlecode.wicket.jquery.core.Options;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.model.IModel;

public abstract class DependentStopEnabledDraggableBehavior<T> extends StopEnabledDraggableBehavior {
	protected final IModel<T> model;

	public DependentStopEnabledDraggableBehavior(IModel<T> model, Options options) {
		super(options);
		this.model = model;
	}

	@Override
	protected void onStop(AjaxRequestTarget target, int left, int top) {
		onStop(target, model.getObject(), left, top);
	}

	protected abstract void onStop(AjaxRequestTarget target, T object, int left, int top);


	@Override
	public void detach(IPartialPageRequestHandler handler) {
		super.detach(handler);
		model.detach();
	}
}
