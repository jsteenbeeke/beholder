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
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.SequenceGenerator;

import com.jeroensteenbeeke.hyperion.data.BaseDomainObject;
import com.jeroensteenbeeke.hyperion.webcomponents.entitypage.DefaultFieldType;
import com.jeroensteenbeeke.hyperion.webcomponents.entitypage.annotation.EntityFormField;

import org.jetbrains.annotations.NotNull;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Nullable;

@Entity
public class MapLink extends BaseDomainObject {


	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_MapLink", name = "MapLink", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "MapLink", strategy = GenerationType.SEQUENCE)
	@Access(value = AccessType.PROPERTY)

	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "source_group_id")
	private FogOfWarGroup sourceGroup;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "target_group_id")
	private FogOfWarGroup targetGroup;

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
	public FogOfWarGroup getSourceGroup() {
		return sourceGroup;
	}

	public void setSourceGroup(@NotNull FogOfWarGroup sourceGroup) {
		this.sourceGroup = sourceGroup;
	}

	@NotNull
	public FogOfWarGroup getTargetGroup() {
		return targetGroup;
	}

	public void setTargetGroup(@NotNull FogOfWarGroup targetGroup) {
		this.targetGroup = targetGroup;
	}
}
