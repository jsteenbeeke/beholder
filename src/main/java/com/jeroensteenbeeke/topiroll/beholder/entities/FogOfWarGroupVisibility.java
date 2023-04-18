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
package com.jeroensteenbeeke.topiroll.beholder.entities;

import com.jeroensteenbeeke.topiroll.beholder.web.data.visitors.FogOfWarShapeContainsVisitor;

import org.jetbrains.annotations.NotNull;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class FogOfWarGroupVisibility extends FogOfWarVisibility {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "group_id")
	private FogOfWarGroup group;

	@NotNull
	public FogOfWarGroup getGroup() {
		return group;
	}

	public void setGroup(@NotNull FogOfWarGroup group) {
		this.group = group;
	}

	@Override
	public boolean containsCoordinate(int x, int y) {
		return group.getShapes().stream().anyMatch(s -> s.visit(new FogOfWarShapeContainsVisitor(x, y)));
	}
}
