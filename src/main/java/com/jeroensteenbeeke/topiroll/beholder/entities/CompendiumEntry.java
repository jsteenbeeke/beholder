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
package com.jeroensteenbeeke.topiroll.beholder.entities;

import java.io.Serializable;
import javax.persistence.*;

import com.jeroensteenbeeke.hyperion.data.BaseDomainObject;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.ArrayList;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

@Entity
public class CompendiumEntry extends BaseDomainObject {


	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_CompendiumEntry", name = "CompendiumEntry", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "CompendiumEntry", strategy = GenerationType.SEQUENCE)
	@Access(value = AccessType.PROPERTY)

	private Long id;
	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	@Lob
	private String body;

 	@ManyToOne(fetch=FetchType.LAZY, optional=true) 	@JoinColumn(name="campaign")
	private Campaign campaign;


	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "author")
	private BeholderUser author;


	@OneToMany(mappedBy = "entry", fetch = FetchType.LAZY)
	private List<PinnedCompendiumEntry> pinnedBy = new ArrayList<PinnedCompendiumEntry>();


	@Column(nullable = true)
	private String originalPath;


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
	public String getTitle() {
		return title;
	}

	public void setTitle(@Nonnull String title) {
		this.title = title;
	}

	@Nonnull
	public String getBody() {
		return body;
	}

	public void setBody(@Nonnull String body) {
		this.body = body;
	}

	@CheckForNull
	public String getOriginalPath() {
		return originalPath;
	}

	public void setOriginalPath(@Nullable String originalPath) {
		this.originalPath = originalPath;
	}

	@Nonnull
	public List<PinnedCompendiumEntry> getPinnedBy() {
		return pinnedBy;
	}

	public void setPinnedBy(@Nonnull List<PinnedCompendiumEntry> pinnedBy) {
		this.pinnedBy = pinnedBy;
	}

	@CheckForNull
	public BeholderUser getAuthor() {
		return author;
	}

	public void setAuthor(@Nullable BeholderUser author) {
		this.author = author;
	}

	@CheckForNull
	public Campaign getCampaign() {
		return campaign;
	}
	public void setCampaign( @Nullable Campaign campaign) {
		this.campaign = campaign;
	}




}
