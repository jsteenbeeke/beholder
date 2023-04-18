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

import java.io.Serializable;

import org.jetbrains.annotations.NotNull;
import javax.persistence.*;

import com.jeroensteenbeeke.topiroll.beholder.entities.visitor.AreaMarkerVisitor;
import org.apache.wicket.markup.html.panel.Panel;

import com.jeroensteenbeeke.hyperion.data.BaseDomainObject;
import com.jeroensteenbeeke.topiroll.beholder.web.data.JSAreaMarker;
import com.jeroensteenbeeke.topiroll.beholder.web.data.shapes.JSShape;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AreaMarker extends BaseDomainObject {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_AreaMarker", name = "AreaMarker",
			initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "AreaMarker",
			strategy = GenerationType.SEQUENCE)
	@Access(value = AccessType.PROPERTY)

	private Long id;

	@Column(nullable = false)
	private String color;

	@Column(nullable = false)
	private int extent;

	@Column(nullable = false)
	private int offsetY;

	@Column(nullable = false)
	private int offsetX;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "view")

	private MapView view;

	public Long getId() {
		return id;
	}

	public void setId(@NotNull Long id) {
		this.id = id;
	}

	@Override
	public final Serializable getDomainObjectId() {
		return getId();
	}

	@NotNull
	public MapView getView() {
		return view;
	}

	public void setView(@NotNull MapView view) {
		this.view = view;
	}

	public int getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(int offsetX) {
		this.offsetX = offsetX;
	}


	public int getOffsetY() {
		return offsetY;
	}

	public void setOffsetY( int offsetY) {
		this.offsetY = offsetY;
	}


	public int getExtent() {
		return extent;
	}

	public void setExtent( int extent) {
		this.extent = extent;
	}


	public String getColor() {
		return color;
	}

	public void setColor( String color) {
		this.color = color;
	}

	public abstract <R> R visit(AreaMarkerVisitor<R> visitor);
}
