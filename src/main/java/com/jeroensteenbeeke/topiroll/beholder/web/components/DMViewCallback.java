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

import com.jeroensteenbeeke.hyperion.data.DomainObject;
import com.jeroensteenbeeke.topiroll.beholder.entities.AreaMarker;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;
import org.apache.wicket.ajax.AjaxRequestTarget;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.io.Serializable;
import java.util.Optional;

public interface DMViewCallback extends Serializable {
	<T extends DomainObject> void createModalWindow(
			@Nonnull
					AjaxRequestTarget target,
			@Nonnull
					PanelConstructor<T>
					constructor,
			@Nullable
					T object);

	<T extends DomainObject> void createModalWindow(
			@Nonnull
					AjaxRequestTarget target,
			@Nonnull
					WindowConstructor<T>
					constructor,
			@Nullable
					T object);

	void redrawMap(AjaxRequestTarget target);

	void refreshMenus(AjaxRequestTarget target);

	TokenInstance getSelectedToken();

	AreaMarker getSelectedMarker();

	void removeModal(AjaxRequestTarget target);

	Optional<Point> getClickedLocation();

	Optional<Point> getPreviousClickedLocation();

	@FunctionalInterface
	interface PanelConstructor<D extends DomainObject> {
		DMViewPanel<D> apply(String id, D object, DMViewCallback callback);
	}

	@FunctionalInterface
	interface WindowConstructor<D extends DomainObject> {
		DMModalWindow<D> apply(String id, D object, DMViewCallback callback);
	}
}
