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

import org.jetbrains.annotations.NotNull;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;

@Entity
public class PinnedCompendiumEntry extends BaseDomainObject {


	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_PinnedCompendiumEntry", name = "PinnedCompendiumEntry", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "PinnedCompendiumEntry", strategy = GenerationType.SEQUENCE)
	@Access(value = AccessType.PROPERTY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "pinnedBy")
	private BeholderUser pinnedBy;


	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "entry")
	private CompendiumEntry entry;

	protected PinnedCompendiumEntry() {
	}

	public PinnedCompendiumEntry(BeholderUser pinnedBy, CompendiumEntry entry) {
		this.pinnedBy = pinnedBy;
		this.entry = entry;
	}

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
	public CompendiumEntry getEntry() {
		return entry;
	}

	public void setEntry(@NotNull CompendiumEntry entry) {
		this.entry = entry;
	}

	@NotNull
	public BeholderUser getPinnedBy() {
		return pinnedBy;
	}

	public void setPinnedBy(@NotNull BeholderUser pinnedBy) {
		this.pinnedBy = pinnedBy;
	}


}
