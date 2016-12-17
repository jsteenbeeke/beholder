/**
 * This file is part of Beholder
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jeroensteenbeeke.topiroll.beholder.beans;

import javax.annotation.Nonnull;

import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.util.JavaScriptHandler;

public interface IMapRenderer {
	/**
	 * Indicates when this renderer should execute relative to other renderes.
	 * Executed in ascending order
	 * 
	 * @return The priority of this renderer
	 */
	int getPriority();

	void onRefresh(@Nonnull String canvasId, @Nonnull JavaScriptHandler handler,
			@Nonnull MapView mapView, boolean previewMode);
}
