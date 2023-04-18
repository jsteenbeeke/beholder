/*
 * This file is part of Beholder
 * Copyright (C) 2016 - 2023 Jeroen Steenbeeke
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

import com.jeroensteenbeeke.hyperion.data.DomainObject;
import com.jeroensteenbeeke.hyperion.webcomponents.core.TypedPanel;
import org.apache.wicket.model.IModel;

public abstract class DMViewPanel<T extends DomainObject> extends TypedPanel<T> {
	private static final long serialVersionUID = 3364544432600465989L;

	protected DMViewPanel(String id) {
		super(id);
		setOutputMarkupPlaceholderTag(true);
	}

	protected DMViewPanel(String id, IModel<T> model) {
		super(id, model);
		setOutputMarkupPlaceholderTag(true);
	}
}
