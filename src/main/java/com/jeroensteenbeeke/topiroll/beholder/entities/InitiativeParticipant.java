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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;

import com.jeroensteenbeeke.hyperion.data.BaseDomainObject;
import com.jeroensteenbeeke.topiroll.beholder.web.data.InitiativeParticipantRenderable;

@Entity
public class InitiativeParticipant extends BaseDomainObject {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_InitiativeParticipant",
			name = "InitiativeParticipant", initialValue = 1,
			allocationSize = 1)
	@GeneratedValue(generator = "InitiativeParticipant",
			strategy = GenerationType.SEQUENCE)
	@Access(value = AccessType.PROPERTY)

	private Long id;
	
 	@Column(nullable=false)
	private InitiativeType initiativeType;
 	@Column(nullable=true)
	private Integer orderOverride;
 	@Column(nullable=false)
	private boolean selected;







	@Column(nullable = true)
	private Integer total;

	@Column(nullable = false)
	private int score;

	@Column(nullable = false)
	private String name;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "view")

	private MapView view;

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
	public MapView getView() {
		return view;
	}

	public void setView(@Nonnull MapView view) {
		this.view = view;
	}

	@Nonnull
	public String getName() {
		return name;
	}

	public void setName(@Nonnull String name) {
		this.name = name;
	}

	@Nonnull
	public int getScore() {
		return score;
	}

	public void setScore(@Nonnull int score) {
		this.score = score;
	}

	@CheckForNull
	public Integer getTotal() {
		return total;
	}

	public void setTotal(@Nullable Integer total) {
		this.total = total;
	}

	public InitiativeParticipantRenderable toJS() {
		InitiativeParticipantRenderable js = new InitiativeParticipantRenderable();
		
		js.setName(getName());
		js.setScore(getTotal());
		js.setSelected(isSelected());
		
		return js;
	}

	@Nonnull
	public InitiativeType getInitiativeType() {
		return initiativeType;
	}
	public void setInitiativeType( @Nonnull InitiativeType initiativeType) {
		this.initiativeType = initiativeType;
	}

	@CheckForNull
	public Integer getOrderOverride() {
		return orderOverride;
	}
	public void setOrderOverride( @Nullable Integer orderOverride) {
		this.orderOverride = orderOverride;
	}

	@Nonnull
	public boolean isSelected() {
		return selected;
	}
	public void setSelected( @Nonnull boolean selected) {
		this.selected = selected;
	}







}
