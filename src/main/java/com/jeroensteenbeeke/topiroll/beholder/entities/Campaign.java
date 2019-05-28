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

import javax.annotation.Nonnull;
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

	public void setId(@Nonnull Long id) {
		this.id = id;
	}

	@Override
	public final Serializable getDomainObjectId() {
		return getId();
	}

	@Nonnull
	public String getName() {
		return name;
	}

	public void setName(@Nonnull String name) {
		this.name = name;
	}

	@Nonnull
	public BeholderUser getDungeonMaster() {
		return dungeonMaster;
	}

	public void setDungeonMaster(@Nonnull BeholderUser dungeonMaster) {
		this.dungeonMaster = dungeonMaster;
	}

	@Nonnull
	public List<TokenDefinition> getTokenDefinitions() {
		return tokenDefinitions;
	}

	public void setTokenDefinitions(@Nonnull List<TokenDefinition> tokenDefinitions) {
		this.tokenDefinitions = tokenDefinitions;
	}

	@Nonnull
	public List<MapFolder> getFolders() {
		return folders;
	}

	public void setFolders(@Nonnull List<MapFolder> folders) {
		this.folders = folders;
	}

	@Nonnull
	public List<ScaledMap> getMaps() {
		return maps;
	}

	public void setMaps(@Nonnull List<ScaledMap> maps) {
		this.maps = maps;
	}

	@Nonnull
	public List<CompendiumEntry> getCompendiumEntries() {
		return compendiumEntries;
	}

	public void setCompendiumEntries(@Nonnull List<CompendiumEntry> compendiumEntries) {
		this.compendiumEntries = compendiumEntries;
	}

	@Nonnull
	public List<YouTubePlaylist> getPlaylists() {
		return playlists;
	}

	public void setPlaylists(@Nonnull List<YouTubePlaylist> playlists) {
		this.playlists = playlists;
	}

	@Nonnull
	public List<Portrait> getPortraits() {
		return portraits;
	}
	public void setPortraits( @Nonnull List<Portrait> portraits) {
		this.portraits = portraits;
	}




}
