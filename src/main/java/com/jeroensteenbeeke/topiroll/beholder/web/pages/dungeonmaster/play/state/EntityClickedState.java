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
package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.play.state;

import com.jeroensteenbeeke.hyperion.data.DomainObject;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import org.apache.wicket.model.IModel;

import org.jetbrains.annotations.NotNull;

public abstract class EntityClickedState<T extends DomainObject> implements IMapViewState {
	private static final long serialVersionUID = 1L;

	private final IModel<T> entity;

	protected EntityClickedState(@NotNull T entity) {
		this.entity = ModelMaker.wrap(entity);
	}

	public T getEntity() {
		return entity.getObject();
	}

	@Override
	public void detach() {
		entity.detach();
	}
}
