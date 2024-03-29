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
package com.jeroensteenbeeke.topiroll.beholder.web.data;

import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;

import java.util.List;
import java.util.stream.Collectors;

public class UpdatePortraits implements JSRenderable {
	public UpdatePortraits() {
	}

	public UpdatePortraits(MapView view) {
		this.portraits = view.getPortraitVisibilities().stream().map(JSPortrait::new).collect(Collectors.toList());
	}

	private List<JSPortrait> portraits;

	public List<JSPortrait> getPortraits() {
		return portraits;
	}

	public void setPortraits(List<JSPortrait> portraits) {
		this.portraits = portraits;
	}

	@Override
	public String getType() {
		return "portraits";
	}
}
