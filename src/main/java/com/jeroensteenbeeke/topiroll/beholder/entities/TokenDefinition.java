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

import java.io.Serializable;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.persistence.*;

import com.jeroensteenbeeke.hyperion.data.BaseDomainObject;
import com.jeroensteenbeeke.hyperion.webcomponents.entitypage.DefaultFieldType;
import com.jeroensteenbeeke.hyperion.webcomponents.entitypage.annotation.EntityFormField;
import com.jeroensteenbeeke.hyperion.webcomponents.entitypage.annotation.Minimum;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

@Entity
public class TokenDefinition extends BaseDomainObject implements AmazonStored {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_TokenDefinition",
			name = "TokenDefinition", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "TokenDefinition",
			strategy = GenerationType.SEQUENCE)
	@Access(value = AccessType.PROPERTY)
	private Long id;

	@Column(nullable = false)
	@EntityFormField(label = "Name", required = true)
	private String name;

	@ManyToOne(fetch=FetchType.LAZY, optional=true)
	@JoinColumn(name="campaign")
	@EntityFormField(label = "Campaign", required = false, type = DefaultFieldType.DropDownChoice.class)
	private Campaign campaign;


 	@Column(nullable=true, name = "amazon_key")
	private String amazonKey;

	@Column(nullable = false)
	@EntityFormField(label = "Diameter", required = true,
			type = DefaultFieldType.Number.class)
	@Minimum(1)
	private int diameterInSquares;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "owner")
	private BeholderUser owner;

	@OneToMany(mappedBy = "definition", fetch = FetchType.LAZY)
	private List<TokenInstance> instances = new ArrayList<TokenInstance>();

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
	public List<TokenInstance> getInstances() {
		return instances;
	}

	public void setInstances(@Nonnull List<TokenInstance> instances) {
		this.instances = instances;
	}

	@Nonnull
	public BeholderUser getOwner() {
		return owner;
	}

	public void setOwner(@Nonnull BeholderUser owner) {
		this.owner = owner;
	}

	@Nonnull
	public String getName() {
		return name;
	}

	public void setName(@Nonnull String name) {
		this.name = name;
	}

	@Nonnull
	public int getDiameterInSquares() {
		return diameterInSquares;
	}

	public void setDiameterInSquares(@Nonnull int diameterInSquares) {
		this.diameterInSquares = diameterInSquares;
	}

	@CheckForNull
	@Override
	public String getAmazonKey() {
		return amazonKey;
	}

	public void setAmazonKey( @Nullable String amazonKey) {
		this.amazonKey = amazonKey;
	}

	@CheckForNull
	public Campaign getCampaign() {
		return campaign;
	}
	public void setCampaign( @Nullable Campaign campaign) {
		this.campaign = campaign;
	}





}
