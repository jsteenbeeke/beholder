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
import java.util.List;
import java.util.ArrayList;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import java.time.LocalDate;

	@Entity 
public class SessionLogIndex extends BaseDomainObject {



	private static final long serialVersionUID = 1L;
 	@Id 	@SequenceGenerator(sequenceName="SEQ_ID_SessionLogIndex", name="SessionLogIndex", initialValue=1, allocationSize=1)
 	@GeneratedValue(generator="SessionLogIndex", strategy=GenerationType.SEQUENCE)
 	@Access(value=AccessType.PROPERTY)

	private Long id;
 	@OneToMany(mappedBy="logIndex", fetch=FetchType.LAZY)
	private List<SessionLogItem> items = new ArrayList<SessionLogItem>();
 	@Column(nullable=false, name = "sessionday")
	private LocalDate day;


 	@ManyToOne(fetch=FetchType.LAZY, optional=false) 	@JoinColumn(name="owner")

	private BeholderUser owner;








	public Long getId() {
		return id;
	}
	public void setId( @NotNull Long id) {
		this.id = id;
	}

	@Override
	public final  Serializable getDomainObjectId() {
		return getId();
	}

	@NotNull
	public List<SessionLogItem> getItems() {
		return items;
	}
	public void setItems( @NotNull List<SessionLogItem> items) {
		this.items = items;
	}

	@NotNull
	public BeholderUser getOwner() {
		return owner;
	}
	public void setOwner( @NotNull BeholderUser owner) {
		this.owner = owner;
	}

	@NotNull
	public LocalDate getDay() {
		return day;
	}
	public void setDay( @NotNull LocalDate day) {
		this.day = day;
	}









}
