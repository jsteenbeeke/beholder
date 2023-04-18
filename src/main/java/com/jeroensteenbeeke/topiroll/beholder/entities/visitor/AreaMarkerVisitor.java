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
package com.jeroensteenbeeke.topiroll.beholder.entities.visitor;

import com.jeroensteenbeeke.topiroll.beholder.entities.CircleMarker;
import com.jeroensteenbeeke.topiroll.beholder.entities.ConeMarker;
import com.jeroensteenbeeke.topiroll.beholder.entities.CubeMarker;
import com.jeroensteenbeeke.topiroll.beholder.entities.LineMarker;

import org.jetbrains.annotations.NotNull;
import java.io.Serializable;

public interface AreaMarkerVisitor<R> extends Serializable {
	R visit(@NotNull CircleMarker marker);

	R visit(@NotNull ConeMarker marker);

	R visit(@NotNull CubeMarker marker);

	R visit(@NotNull LineMarker marker);
}
