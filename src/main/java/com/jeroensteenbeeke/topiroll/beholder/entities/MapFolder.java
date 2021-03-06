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
package com.jeroensteenbeeke.topiroll.beholder.entities;

import com.jeroensteenbeeke.hyperion.data.BaseDomainObject;
import com.jeroensteenbeeke.hyperion.webcomponents.entitypage.DefaultFieldType;
import com.jeroensteenbeeke.hyperion.webcomponents.entitypage.annotation.EntityFormField;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class MapFolder extends BaseDomainObject {

	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_MapFolder", name = "MapFolder", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "MapFolder", strategy = GenerationType.SEQUENCE)
	@Access(value = AccessType.PROPERTY)
	private Long id;
 	@ManyToOne(fetch=FetchType.LAZY, optional=false) 	@JoinColumn(name="owner")

	private BeholderUser owner;




	@OneToMany(mappedBy = "folder", fetch = FetchType.LAZY)
	private List<ScaledMap> maps = new ArrayList<ScaledMap>();

	@OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
	private List<MapFolder> children = new ArrayList<MapFolder>();

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "parent")
	private MapFolder parent;

	@Column(nullable = false)
	@EntityFormField(label = "Name", required = true)
	private String name;


	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "campaign")
	@EntityFormField(required = false, label = "Campaign", type = DefaultFieldType.DropDownChoice.class)
	private Campaign campaign;


	public Long getId() {
		return id;
	}

	public void setId(@Nonnull Long id) {
		this.id = id;
	}

	@Override
	public final Serializable getDomainObjectId() {
		return getId();
	}

	@Nonnull
	public String getName() {
		return name;
	}

	public void setName(@Nonnull String name) {
		this.name = name;
	}

	@CheckForNull
	public MapFolder getParent() {
		return parent;
	}

	public void setParent(@Nullable MapFolder parent) {
		this.parent = parent;
	}

	@Nonnull
	public List<MapFolder> getChildren() {
		return children;
	}

	public void setChildren(@Nonnull List<MapFolder> children) {
		this.children = children;
	}

	@Nonnull
	public List<ScaledMap> getMaps() {
		return maps;
	}

	public void setMaps(@Nonnull List<ScaledMap> maps) {
		this.maps = maps;
	}

	public String getNameWithParents() {
		if (parent != null) {
			return String
				.format("%s \\ %s", parent.getNameWithParents(), getName());
		}

		return getName();
	}

	@CheckForNull
	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(@Nullable Campaign campaign) {
		this.campaign = campaign;
	}

	@Nonnull
	public BeholderUser getOwner() {
		return owner;
	}
	public void setOwner( @Nonnull BeholderUser owner) {
		this.owner = owner;
	}




}
