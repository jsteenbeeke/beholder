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
package com.jeroensteenbeeke.topiroll.beholder.web.model;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

public abstract class DependentModel<D,T> extends LoadableDetachableModel<T> {
	private static final long serialVersionUID = 2583546773047186979L;

	private final IModel<D> referencedModel;

	public DependentModel(IModel<D> referencedModel) {
		this.referencedModel = referencedModel;
	}

	@Override
	protected final T load() {
		return load(referencedModel.getObject());
	}

	protected abstract T load(D object);


	@Override
	public void detach() {
		super.detach();

		referencedModel.detach();
	}
}
