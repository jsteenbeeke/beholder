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
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.jetbrains.annotations.NotNull;
import jakarta.persistence.*;

import com.jeroensteenbeeke.hyperion.data.BaseDomainObject;
import io.vavr.control.Option;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Nullable;

@Entity
public class BeholderUser extends BaseDomainObject {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_BeholderUser",
			name = "BeholderUser", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "BeholderUser",
			strategy = GenerationType.SEQUENCE)
	@Access(value = AccessType.PROPERTY)

	private Long id;

	@Column(nullable = false)
	private String accessToken;

	@Column(nullable = false)
	private String userId;

	@Column(nullable = false)
	private String username;
 	@OneToMany(mappedBy="owner", fetch=FetchType.LAZY)
	private List<SessionLogIndex> sessionLogIndices = new ArrayList<SessionLogIndex>();


 	@OneToMany(mappedBy="user", fetch=FetchType.LAZY)
	private List<SessionLogItem> logItems = new ArrayList<SessionLogItem>();


 	@OneToMany(mappedBy="owner", fetch=FetchType.LAZY)
	private List<MapFolder> mapFolders = new ArrayList<MapFolder>();


 	@ManyToOne(fetch=FetchType.LAZY, optional=true) 	@JoinColumn(name="activeCampaign")

	private Campaign activeCampaign;


 	@OneToMany(mappedBy="dungeonMaster", fetch=FetchType.LAZY)
	private List<Campaign> campaigns = new ArrayList<Campaign>();


 	@OneToMany(mappedBy="author", fetch=FetchType.LAZY)
	private List<CompendiumEntry> writtenEntries = new ArrayList<CompendiumEntry>();


 	@OneToMany(mappedBy="pinnedBy", fetch=FetchType.LAZY)
	private List<PinnedCompendiumEntry> pinnedEntries = new ArrayList<PinnedCompendiumEntry>();


 	@OneToMany(mappedBy="owner", fetch=FetchType.LAZY)
	private List<YouTubePlaylist> playlists = new ArrayList<YouTubePlaylist>();


 	@OneToMany(mappedBy="owner", fetch=FetchType.LAZY)
	private List<Portrait> portraits = new ArrayList<Portrait>();


 	@OneToMany(mappedBy="owner", fetch=FetchType.LAZY)
	private List<TokenDefinition> tokens = new ArrayList<TokenDefinition>();



	@OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
	private List<ScaledMap> maps = new ArrayList<ScaledMap>();

	@OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
	private List<MapView> views = new ArrayList<MapView>();

	@Column(nullable = false)
	private String avatar;

	@Column(nullable = false)
	private String teamId;

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
	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(@NotNull String accessToken) {
		this.accessToken = accessToken;
	}

	@NotNull
	public String getUserId() {
		return userId;
	}

	public void setUserId(@NotNull String userId) {
		this.userId = userId;
	}

	@NotNull
	public String getTeamId() {
		return teamId;
	}

	public void setTeamId(@NotNull String teamId) {
		this.teamId = teamId;
	}

	@NotNull
	public String getUsername() {
		return username;
	}

	public void setUsername(@NotNull String username) {
		this.username = username;
	}

	@NotNull
	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(@NotNull String avatar) {
		this.avatar = avatar;
	}

	@NotNull
	public List<MapView> getViews() {
		return views;
	}

	public void setViews(@NotNull List<MapView> views) {
		this.views = views;
	}

	@NotNull
	public List<ScaledMap> getMaps() {
		return maps;
	}

	public void setMaps(@NotNull List<ScaledMap> maps) {
		this.maps = maps;
	}

	@NotNull
	public List<TokenDefinition> getTokens() {
		return tokens;
	}
	public void setTokens( @NotNull List<TokenDefinition> tokens) {
		this.tokens = tokens;
	}

	@NotNull
	public List<Portrait> getPortraits() {
		return portraits;
	}
	public void setPortraits( @NotNull List<Portrait> portraits) {
		this.portraits = portraits;
	}

	@NotNull
	public List<YouTubePlaylist> getPlaylists() {
		return playlists;
	}
	public void setPlaylists( @NotNull List<YouTubePlaylist> playlists) {
		this.playlists = playlists;
	}

	@NotNull
	public List<PinnedCompendiumEntry> getPinnedEntries() {
		return pinnedEntries;
	}
	public void setPinnedEntries( @NotNull List<PinnedCompendiumEntry> pinnedEntries) {
		this.pinnedEntries = pinnedEntries;
	}

	@NotNull
	public List<CompendiumEntry> getWrittenEntries() {
		return writtenEntries;
	}
	public void setWrittenEntries( @NotNull List<CompendiumEntry> writtenEntries) {
		this.writtenEntries = writtenEntries;
	}

	@NotNull
	public List<Campaign> getCampaigns() {
		return campaigns;
	}
	public void setCampaigns( @NotNull List<Campaign> campaigns) {
		this.campaigns = campaigns;
	}

	@Nullable
	public Campaign getActiveCampaign() {
		return activeCampaign;
	}
	public void setActiveCampaign( @Nullable Campaign activeCampaign) {
		this.activeCampaign = activeCampaign;
	}


	@Transient
	@NotNull
	public Option<Campaign> activeCampaign() {
		return Option.of(getActiveCampaign());
	}

	@NotNull
	public List<MapFolder> getMapFolders() {
		return mapFolders;
	}
	public void setMapFolders( @NotNull List<MapFolder> mapFolders) {
		this.mapFolders = mapFolders;
	}

	@NotNull
	public List<SessionLogItem> getLogItems() {
		return logItems;
	}
	public void setLogItems( @NotNull List<SessionLogItem> logItems) {
		this.logItems = logItems;
	}

	@NotNull
	public List<SessionLogIndex> getSessionLogIndices() {
		return sessionLogIndices;
	}
	public void setSessionLogIndices( @NotNull List<SessionLogIndex> sessionLogIndices) {
		this.sessionLogIndices = sessionLogIndices;
	}






}
