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
import javax.persistence.*;

import com.jeroensteenbeeke.hyperion.data.BaseDomainObject;

import org.jetbrains.annotations.NotNull;
import java.time.LocalDateTime;

@Entity
public class SessionLogItem extends BaseDomainObject {


	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_SessionLogItem", name = "SessionLogItem", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "SessionLogItem", strategy = GenerationType.SEQUENCE)
	@Access(value = AccessType.PROPERTY)

	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id")
	private BeholderUser user;
 	@Column(nullable=false)
	private boolean completed;


 	@ManyToOne(fetch=FetchType.LAZY, optional=false) 	@JoinColumn(name="logIndex")

	private SessionLogIndex logIndex;



	@Column(nullable = false)
	@Lob
	private String eventDescription;

	@Column(nullable = false)
	private LocalDateTime eventTime;


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
	public BeholderUser getUser() {
		return user;
	}

	public void setUser(@NotNull BeholderUser user) {
		this.user = user;
	}

	@NotNull
	public LocalDateTime getEventTime() {
		return eventTime;
	}

	public void setEventTime(@NotNull LocalDateTime eventTime) {
		this.eventTime = eventTime;
	}

	@NotNull
	public String getEventDescription() {
		return eventDescription;
	}

	public void setEventDescription(@NotNull String eventDescription) {
		this.eventDescription = eventDescription;
	}

	@NotNull
	public SessionLogIndex getLogIndex() {
		return logIndex;
	}
	public void setLogIndex( @NotNull SessionLogIndex logIndex) {
		this.logIndex = logIndex;
	}

	@NotNull
	public boolean isCompleted() {
		return completed;
	}
	public void setCompleted( @NotNull boolean completed) {
		this.completed = completed;
	}






}
