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
import com.jeroensteenbeeke.hyperion.webcomponents.entitypage.DefaultFieldType;
import com.jeroensteenbeeke.hyperion.webcomponents.entitypage.annotation.EntityFormField;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import jakarta.persistence.*;
import java.io.Serializable;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Portrait extends BaseDomainObject implements AmazonStored {

	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_Portrait", name = "Portrait", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "Portrait", strategy = GenerationType.SEQUENCE)
	@Access(value = AccessType.PROPERTY)
	private Long id;

	@Column(nullable = false)
	@EntityFormField(label = "Name", required = true)
	private String name;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "campaign")
	@EntityFormField(label = "Campaign", type = DefaultFieldType.DropDownChoice.class)
	private Campaign campaign;

	@Column(name = "amazon_key")
	private String amazonKey;

	@OneToMany(mappedBy = "portrait", fetch = FetchType.LAZY)
	private List<PortraitVisibility> visibilities = new ArrayList<PortraitVisibility>();

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
	public List<PortraitVisibility> getVisibilities() {
		return visibilities;
	}

	public void setVisibilities(
		@NotNull List<PortraitVisibility> visibilities) {
		this.visibilities = visibilities;
	}

	@Nullable
	@Override
	public String getAmazonKey() {
		return amazonKey;
	}

	public void setAmazonKey(@Nullable String amazonKey) {
		this.amazonKey = amazonKey;
	}

	@Nullable
	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(@Nullable Campaign campaign) {
		this.campaign = campaign;
	}

}
