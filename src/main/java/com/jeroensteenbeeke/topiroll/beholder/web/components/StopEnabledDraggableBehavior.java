/**
 * This file is part of Beholder
 * (C) 2016-2019 Jeroen Steenbeeke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jeroensteenbeeke.topiroll.beholder.web.components;

import com.googlecode.wicket.jquery.core.JQueryEvent;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableAdapter;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;

public abstract class StopEnabledDraggableBehavior extends DraggableBehavior {
	private static final long serialVersionUID = 1604277314065355628L;

	public StopEnabledDraggableBehavior(Options options) {
		super(options, new StopEnabledDraggableAdapter());
	}

	@Override
	public void onAjax(AjaxRequestTarget target, JQueryEvent event) {
		super.onAjax(target, event);
		if (event instanceof DragStopEvent) {
			DragStopEvent stop = (DragStopEvent) event;
			onStop(target, stop.getLeft(), stop.getTop());
		}
	}

	protected abstract void onStop(AjaxRequestTarget target, int left, int top);

	private static class StopEnabledDraggableAdapter extends DraggableAdapter {
		private static final long serialVersionUID = 135491738554139810L;

		@Override
		public boolean isStopEventEnabled() {
			return true;
		}
	}
}
