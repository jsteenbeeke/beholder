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
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import javax.annotation.Nonnull;
import javax.persistence.*;

import com.jeroensteenbeeke.hyperion.data.BaseDomainObject;
import io.vavr.control.Option;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

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

	public void setId(@Nonnull Long id) {
		this.id = id;
	}

	@Override
	public final Serializable getDomainObjectId() {
		return getId();
	}

	@Nonnull
	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(@Nonnull String accessToken) {
		this.accessToken = accessToken;
	}

	@Nonnull
	public String getUserId() {
		return userId;
	}

	public void setUserId(@Nonnull String userId) {
		this.userId = userId;
	}

	@Nonnull
	public String getTeamId() {
		return teamId;
	}

	public void setTeamId(@Nonnull String teamId) {
		this.teamId = teamId;
	}

	@Nonnull
	public String getUsername() {
		return username;
	}

	public void setUsername(@Nonnull String username) {
		this.username = username;
	}

	@Nonnull
	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(@Nonnull String avatar) {
		this.avatar = avatar;
	}

	@Nonnull
	public List<MapView> getViews() {
		return views;
	}

	public void setViews(@Nonnull List<MapView> views) {
		this.views = views;
	}

	@Nonnull
	public List<ScaledMap> getMaps() {
		return maps;
	}

	public void setMaps(@Nonnull List<ScaledMap> maps) {
		this.maps = maps;
	}

	@Nonnull
	public List<TokenDefinition> getTokens() {
		return tokens;
	}
	public void setTokens( @Nonnull List<TokenDefinition> tokens) {
		this.tokens = tokens;
	}

	@Nonnull
	public List<Portrait> getPortraits() {
		return portraits;
	}
	public void setPortraits( @Nonnull List<Portrait> portraits) {
		this.portraits = portraits;
	}

	@Nonnull
	public List<YouTubePlaylist> getPlaylists() {
		return playlists;
	}
	public void setPlaylists( @Nonnull List<YouTubePlaylist> playlists) {
		this.playlists = playlists;
	}

	@Nonnull
	public List<PinnedCompendiumEntry> getPinnedEntries() {
		return pinnedEntries;
	}
	public void setPinnedEntries( @Nonnull List<PinnedCompendiumEntry> pinnedEntries) {
		this.pinnedEntries = pinnedEntries;
	}

	@Nonnull
	public List<CompendiumEntry> getWrittenEntries() {
		return writtenEntries;
	}
	public void setWrittenEntries( @Nonnull List<CompendiumEntry> writtenEntries) {
		this.writtenEntries = writtenEntries;
	}

	@Nonnull
	public List<Campaign> getCampaigns() {
		return campaigns;
	}
	public void setCampaigns( @Nonnull List<Campaign> campaigns) {
		this.campaigns = campaigns;
	}

	@CheckForNull
	public Campaign getActiveCampaign() {
		return activeCampaign;
	}
	public void setActiveCampaign( @Nullable Campaign activeCampaign) {
		this.activeCampaign = activeCampaign;
	}


	@Transient
	@Nonnull
	public Option<Campaign> activeCampaign() {
		return Option.of(getActiveCampaign());
	}

	@Nonnull
	public List<MapFolder> getMapFolders() {
		return mapFolders;
	}
	public void setMapFolders( @Nonnull List<MapFolder> mapFolders) {
		this.mapFolders = mapFolders;
	}

	@Nonnull
	public List<SessionLogItem> getLogItems() {
		return logItems;
	}
	public void setLogItems( @Nonnull List<SessionLogItem> logItems) {
		this.logItems = logItems;
	}

	@Nonnull
	public List<SessionLogIndex> getSessionLogIndices() {
		return sessionLogIndices;
	}
	public void setSessionLogIndices( @Nonnull List<SessionLogIndex> sessionLogIndices) {
		this.sessionLogIndices = sessionLogIndices;
	}






}
