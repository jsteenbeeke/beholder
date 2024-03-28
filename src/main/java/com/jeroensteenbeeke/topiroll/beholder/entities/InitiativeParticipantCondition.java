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

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import jakarta.persistence.*;
import java.io.Serializable;

@Entity
public class InitiativeParticipantCondition extends BaseDomainObject {

	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_InitiativeParticipantCondition", name = "InitiativeParticipantCondition", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "InitiativeParticipantCondition", strategy = GenerationType.SEQUENCE)
	@Access(value = AccessType.PROPERTY)

	private Long id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "participant")

	private InitiativeParticipant participant;
	@Column(nullable = true)
	private Integer turnsRemaining;

	@Column(nullable = false)
	private String description;

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
	public InitiativeParticipant getParticipant() {
		return participant;
	}

	public void setParticipant(@NotNull InitiativeParticipant participant) {
		this.participant = participant;
	}

	@NotNull
	public String getDescription() {
		return description;
	}

	public void setDescription(@NotNull String description) {
		this.description = description;
	}

	@Nullable
	public Integer getTurnsRemaining() {
		return turnsRemaining;
	}

	public void setTurnsRemaining(@Nullable Integer turnsRemaining) {
		this.turnsRemaining = turnsRemaining;
	}

}
