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
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;

import com.jeroensteenbeeke.hyperion.data.BaseDomainObject;
import com.jeroensteenbeeke.hyperion.webcomponents.entitypage.DefaultFieldType;
import com.jeroensteenbeeke.hyperion.webcomponents.entitypage.annotation.EntityFormField;

import org.jetbrains.annotations.NotNull;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.FetchType;
import javax.persistence.Column;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Nullable;

@Entity
public class YouTubePlaylist extends BaseDomainObject {


	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_YouTubePlaylist", name = "YouTubePlaylist", initialValue = 1,
		allocationSize = 1)
	@GeneratedValue(generator = "YouTubePlaylist", strategy = GenerationType.SEQUENCE)
	@Access(value = AccessType.PROPERTY)
	private Long id;

	@Column(nullable = false)
	@EntityFormField(label = "Name", required = true)
	private String name;

	@Column(nullable = false)
	@EntityFormField(label = "URL", required = true)
	private String url;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "campaign")
	@EntityFormField(label = "Campaign", required = false, type = DefaultFieldType.DropDownChoice.class)
	private Campaign campaign;

	@Column(nullable = true)
	@EntityFormField(label = "Number of entries", required = false)
	private Integer numberOfEntries;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "owner")
	private BeholderUser owner;


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
	public BeholderUser getOwner() {
		return owner;
	}

	public void setOwner(@NotNull BeholderUser owner) {
		this.owner = owner;
	}

	@NotNull
	public String getName() {
		return name;
	}

	public void setName(@NotNull String name) {
		this.name = name;
	}

	@NotNull
	public String getUrl() {
		return url;
	}

	public void setUrl(@NotNull String url) {
		this.url = url;
	}

	@Nullable
	public Integer getNumberOfEntries() {
		return numberOfEntries;
	}

	public void setNumberOfEntries(@Nullable Integer numberOfEntries) {
		this.numberOfEntries = numberOfEntries;
	}

	@Nullable
	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(@Nullable Campaign campaign) {
		this.campaign = campaign;
	}


}
