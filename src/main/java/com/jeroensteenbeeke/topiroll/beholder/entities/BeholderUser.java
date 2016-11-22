package com.jeroensteenbeeke.topiroll.beholder.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.persistence.*;

import com.jeroensteenbeeke.hyperion.data.BaseDomainObject;

	@Entity 
public class BeholderUser extends BaseDomainObject {



	private static final long serialVersionUID = 1L;
 	@Id 	@SequenceGenerator(sequenceName="SEQ_ID_BeholderUser", name="BeholderUser", initialValue=1, allocationSize=1)
 	@GeneratedValue(generator="BeholderUser", strategy=GenerationType.SEQUENCE)
 	@Access(value=AccessType.PROPERTY)

	private Long id;
 	@Column(nullable=false)
	private String accessToken;
 	@Column(nullable=false)
	private String userId;
 	@Column(nullable=false)
	private String username;
 	@OneToMany(mappedBy="owner", fetch=FetchType.LAZY)
	private List<ScaledMap> maps = new ArrayList<ScaledMap>();


 	@OneToMany(mappedBy="owner", fetch=FetchType.LAZY)
	private List<MapView> views = new ArrayList<MapView>();


 	@Column(nullable=false)
	private String avatar;




 	@Column(nullable=false)
	private String teamId;










	public Long getId() {
		return id;
	}
	public void setId( @Nonnull Long id) {
		this.id = id;
	}

	@Override
	public final  Serializable getDomainObjectId() {
		return getId();
	}

	@Nonnull
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken( @Nonnull String accessToken) {
		this.accessToken = accessToken;
	}

	@Nonnull
	public String getUserId() {
		return userId;
	}
	public void setUserId( @Nonnull String userId) {
		this.userId = userId;
	}

	@Nonnull
	public String getTeamId() {
		return teamId;
	}
	public void setTeamId( @Nonnull String teamId) {
		this.teamId = teamId;
	}

	@Nonnull
	public String getUsername() {
		return username;
	}
	public void setUsername( @Nonnull String username) {
		this.username = username;
	}

	@Nonnull
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar( @Nonnull String avatar) {
		this.avatar = avatar;
	}

	@Nonnull
	public List<MapView> getViews() {
		return views;
	}
	public void setViews( @Nonnull List<MapView> views) {
		this.views = views;
	}

	@Nonnull
	public List<ScaledMap> getMaps() {
		return maps;
	}
	public void setMaps( @Nonnull List<ScaledMap> maps) {
		this.maps = maps;
	}

















}
