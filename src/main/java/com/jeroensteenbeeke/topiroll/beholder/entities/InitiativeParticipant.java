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
import com.jeroensteenbeeke.hyperion.util.HashUtil;
import com.jeroensteenbeeke.topiroll.beholder.web.data.InitiativeParticipantRenderable;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Entity
public class InitiativeParticipant extends BaseDomainObject {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_InitiativeParticipant", name = "InitiativeParticipant", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "InitiativeParticipant", strategy = GenerationType.SEQUENCE)
	@Access(value = AccessType.PROPERTY)

	private Long id;

	@Column(nullable = false)
	private InitiativeType initiativeType;

	@Column(nullable = true)
	private Integer orderOverride;

	@Column(nullable = false)
	private boolean player;
	@OneToMany(mappedBy = "participant", fetch = FetchType.LAZY)
	private List<InitiativeParticipantCondition> conditions = new ArrayList<InitiativeParticipantCondition>();

	@Column(nullable = true)
	private Integer offsetY;

	@Column(nullable = true)
	private Integer offsetX;

	@Column(nullable = false)
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

	public void setId(@NotNull Long id) {
		this.id = id;
	}

	@Override
	public final Serializable getDomainObjectId() {
		return getId();
	}

	@NotNull
	public MapView getView() {
		return view;
	}

	public void setView(@NotNull MapView view) {
		this.view = view;
	}

	@NotNull
	public String getName() {
		return name;
	}

	public void setName(@NotNull String name) {
		this.name = name;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	@Nullable
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

	@NotNull
	public InitiativeType getInitiativeType() {
		return initiativeType;
	}

	public void setInitiativeType(@NotNull InitiativeType initiativeType) {
		this.initiativeType = initiativeType;
	}

	@Nullable
	public Integer getOrderOverride() {
		return orderOverride;
	}

	public void setOrderOverride(@Nullable Integer orderOverride) {
		this.orderOverride = orderOverride;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isPlayer() {
		return player;
	}

	public void setPlayer(boolean player) {
		this.player = player;
	}

	@Nullable
	public Integer getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(@Nullable Integer offsetX) {
		this.offsetX = offsetX;
	}

	@Nullable
	public Integer getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(@Nullable Integer offsetY) {
		this.offsetY = offsetY;
	}

	@NotNull
	public List<InitiativeParticipantCondition> getConditions() {
		return conditions;
	}

	public void setConditions(
		@NotNull List<InitiativeParticipantCondition> conditions) {
		this.conditions = conditions;
	}

	public static boolean hasPosition(InitiativeParticipant participant) {
		return participant.getOffsetX() != null && participant.getOffsetY() != null;
	}
}
