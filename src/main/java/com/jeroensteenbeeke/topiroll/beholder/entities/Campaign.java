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
import com.jeroensteenbeeke.hyperion.webcomponents.entitypage.annotation.EntityFormField;

import org.jetbrains.annotations.NotNull;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.FetchType;
import java.util.List;
import java.util.ArrayList;
import javax.persistence.OneToMany;

@Entity
public class Campaign extends BaseDomainObject {


	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_Campaign", name = "Campaign", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "Campaign", strategy = GenerationType.SEQUENCE)
	@Access(value = AccessType.PROPERTY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "dungeonMaster")
	private BeholderUser dungeonMaster;
 	@OneToMany(mappedBy="campaign", fetch=FetchType.LAZY)
	private List<Portrait> portraits = new ArrayList<Portrait>();


	@OneToMany(mappedBy = "campaign", fetch = FetchType.LAZY)
	private List<YouTubePlaylist> playlists = new ArrayList<>();


	@OneToMany(mappedBy = "campaign", fetch = FetchType.LAZY)
	private List<CompendiumEntry> compendiumEntries = new ArrayList<>();


	@OneToMany(mappedBy = "campaign", fetch = FetchType.LAZY)
	private List<ScaledMap> maps = new ArrayList<>();


	@OneToMany(mappedBy = "campaign", fetch = FetchType.LAZY)
	private List<MapFolder> folders = new ArrayList<>();


	@OneToMany(mappedBy = "campaign", fetch = FetchType.LAZY)
	private List<TokenDefinition> tokenDefinitions = new ArrayList<>();


	@Column(nullable = false)
	@EntityFormField(label = "Name")
	private String name;

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
	public String getName() {
		return name;
	}

	public void setName(@NotNull String name) {
		this.name = name;
	}

	@NotNull
	public BeholderUser getDungeonMaster() {
		return dungeonMaster;
	}

	public void setDungeonMaster(@NotNull BeholderUser dungeonMaster) {
		this.dungeonMaster = dungeonMaster;
	}

	@NotNull
	public List<TokenDefinition> getTokenDefinitions() {
		return tokenDefinitions;
	}

	public void setTokenDefinitions(@NotNull List<TokenDefinition> tokenDefinitions) {
		this.tokenDefinitions = tokenDefinitions;
	}

	@NotNull
	public List<MapFolder> getFolders() {
		return folders;
	}

	public void setFolders(@NotNull List<MapFolder> folders) {
		this.folders = folders;
	}

	@NotNull
	public List<ScaledMap> getMaps() {
		return maps;
	}

	public void setMaps(@NotNull List<ScaledMap> maps) {
		this.maps = maps;
	}

	@NotNull
	public List<CompendiumEntry> getCompendiumEntries() {
		return compendiumEntries;
	}

	public void setCompendiumEntries(@NotNull List<CompendiumEntry> compendiumEntries) {
		this.compendiumEntries = compendiumEntries;
	}

	@NotNull
	public List<YouTubePlaylist> getPlaylists() {
		return playlists;
	}

	public void setPlaylists(@NotNull List<YouTubePlaylist> playlists) {
		this.playlists = playlists;
	}

	@NotNull
	public List<Portrait> getPortraits() {
		return portraits;
	}
	public void setPortraits( @NotNull List<Portrait> portraits) {
		this.portraits = portraits;
	}




}
