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
import jakarta.persistence.*;

import com.jeroensteenbeeke.hyperion.data.BaseDomainObject;

import org.jetbrains.annotations.NotNull;

@Entity
public class PortraitVisibility extends BaseDomainObject {


	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_PortraitVisibility", name = "PortraitVisibility", initialValue = 1,
			allocationSize = 1)
	@GeneratedValue(generator = "PortraitVisibility", strategy = GenerationType.SEQUENCE)
	@Access(value = AccessType.PROPERTY)

	private Long id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "portrait")

	private Portrait portrait;
 	@Column(nullable=false)
	@Enumerated(EnumType.STRING)
	private PortraitVisibilityLocation location;


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
	public Portrait getPortrait() {
		return portrait;
	}

	public void setPortrait(@NotNull Portrait portrait) {
		this.portrait = portrait;
	}

	@NotNull
	public MapView getView() {
		return view;
	}

	public void setView(@NotNull MapView view) {
		this.view = view;
	}

	@NotNull
	public PortraitVisibilityLocation getLocation() {
		return location;
	}

	public void setLocation( @NotNull PortraitVisibilityLocation location) {
		this.location = location;
	}




}
