package com.jeroensteenbeeke.topiroll.beholder.web.components;

import com.googlecode.wicket.jquery.core.JQueryEvent;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableAdapter;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;

public abstract class StopEnabledDraggableBehavior extends DraggableBehavior {
	public StopEnabledDraggableBehavior(Options options) {
		super(options, new StopEnabledDraggableAdapter());
	}

	@Override
	public void onAjax(AjaxRequestTarget target, JQueryEvent event) {
		super.onAjax(target, event);
		if (event instanceof DragStopEvent) {
			DragStopEvent stop = (DragStopEvent) event;
			onStop(target, stop.getOffsetLeft(), stop.getOffsetTop());
		}
	}

	protected abstract void onStop(AjaxRequestTarget target, int left, int top);

	private static class StopEnabledDraggableAdapter extends DraggableAdapter {
		@Override
		public boolean isStopEventEnabled() {
			return true;
		}
	}
}
