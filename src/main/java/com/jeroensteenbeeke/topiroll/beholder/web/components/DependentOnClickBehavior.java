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

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

public abstract class DependentOnClickBehavior<T> extends OnClickBehavior {
	private static final long serialVersionUID = 6481092014135303291L;

	private final IModel<T> model;

	public DependentOnClickBehavior(IModel<T> model) {
		this.model = model;
	}

	@Override
	protected void onClick(AjaxRequestTarget target, ClickEvent event) {
		onClick(target, event, model.getObject());
	}

	protected abstract void onClick(AjaxRequestTarget target, ClickEvent event, T modelObject);

	@Override
	public void detach(Component component) {
		super.detach(component);

		model.detach();
	}
}
