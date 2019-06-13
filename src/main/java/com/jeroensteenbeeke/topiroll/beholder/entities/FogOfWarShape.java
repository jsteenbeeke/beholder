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
/**
 * This file is part of Beholder
 * (C) 2016 Jeroen Steenbeeke
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

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;

import com.jeroensteenbeeke.hyperion.data.BaseDomainObject;
import com.jeroensteenbeeke.topiroll.beholder.entities.visitor.FogOfWarShapeVisitor;
import com.jeroensteenbeeke.topiroll.beholder.web.data.shapes.JSShape;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class FogOfWarShape extends BaseDomainObject {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_FogOfWarShape",
			name = "FogOfWarShape", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "FogOfWarShape",
			strategy = GenerationType.SEQUENCE)
	@Access(value = AccessType.PROPERTY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "map")
	private ScaledMap map;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "group_id")
	private FogOfWarGroup group;


	@OneToMany(mappedBy = "shape", fetch = FetchType.LAZY)
	private List<FogOfWarShapeVisibility> visibilities = new ArrayList<FogOfWarShapeVisibility>();

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
	public ScaledMap getMap() {
		return map;
	}

	public void setMap(@Nonnull ScaledMap map) {
		this.map = map;
	}

	@CheckForNull
	public FogOfWarGroup getGroup() {
		return group;
	}

	public void setGroup(@Nullable FogOfWarGroup group) {
		this.group = group;
	}

	@Transient
	public abstract String getDescription();

	public abstract <T> T visit(@Nonnull FogOfWarShapeVisitor<T> visitor);

	@Nonnull
	public List<FogOfWarShapeVisibility> getVisibilities() {
		return visibilities;
	}

	public void setVisibilities(
			@Nonnull List<FogOfWarShapeVisibility> visibilities) {
		this.visibilities = visibilities;
	}


}
