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
