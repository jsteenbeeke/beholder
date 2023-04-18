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

import com.jeroensteenbeeke.hyperion.data.BaseDomainObject;

import org.jetbrains.annotations.NotNull;
import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class FogOfWarGroup extends BaseDomainObject {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_FogOfWarGroup",
			name = "FogOfWarGroup", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "FogOfWarGroup",
			strategy = GenerationType.SEQUENCE)
	@Access(value = AccessType.PROPERTY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "map")

	private ScaledMap map;
 	@OneToMany(mappedBy="sourceGroup", fetch=FetchType.LAZY)
	private List<MapLink> links = new ArrayList<>();



	@OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
	private List<FogOfWarGroupVisibility> visibilities = new ArrayList<FogOfWarGroupVisibility>();

	@Column(nullable = false)
	private String name;

	@OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
	private List<FogOfWarShape> shapes = new ArrayList<FogOfWarShape>();

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
	public List<FogOfWarShape> getShapes() {
		return shapes;
	}

	public void setShapes(@NotNull List<FogOfWarShape> shapes) {
		this.shapes = shapes;
	}

	@NotNull
	public ScaledMap getMap() {
		return map;
	}

	public void setMap(@NotNull ScaledMap map) {
		this.map = map;
	}

	@NotNull
	public String getName() {
		return name;
	}

	public void setName(@NotNull String name) {
		this.name = name;
	}

	public String getDescription() {
		return getName();
	}

	@NotNull
	public List<FogOfWarGroupVisibility> getVisibilities() {
		return visibilities;
	}

	public void setVisibilities(
			@NotNull List<FogOfWarGroupVisibility> visibilities) {
		this.visibilities = visibilities;
	}

	@NotNull
	public List<MapLink>  getLinks() {
		return links;
	}
	public void setLinks( @NotNull List<MapLink> links) {
		this.links = links;
	}



}
