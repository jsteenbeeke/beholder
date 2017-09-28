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
import com.jeroensteenbeeke.hyperion.ducktape.web.pages.entity.annotation.EntityFormField;

import javax.annotation.Nonnull;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.FetchType;
import javax.persistence.Column;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

@Entity
public class YouTubePlaylist extends BaseDomainObject {


	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_YouTubePlaylist", name = "YouTubePlaylist", initialValue = 1,
			allocationSize = 1)
	@GeneratedValue(generator = "YouTubePlaylist", strategy = GenerationType.SEQUENCE)
	@Access(value = AccessType.PROPERTY)

	private Long id;

	@Column(nullable = false)
	@EntityFormField(label = "Name", required = true)
	private String name;

	@Column(nullable = false)
	@EntityFormField(label = "URL", required = true)
	private String url;

 	@Column(nullable=true)
	@EntityFormField(label = "Number of entries", required = false)
	private Integer numberOfEntries;



	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "owner")

	private BeholderUser owner;


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
	public BeholderUser getOwner() {
		return owner;
	}

	public void setOwner(@Nonnull BeholderUser owner) {
		this.owner = owner;
	}

	@Nonnull
	public String getName() {
		return name;
	}

	public void setName(@Nonnull String name) {
		this.name = name;
	}

	@Nonnull
	public String getUrl() {
		return url;
	}

	public void setUrl(@Nonnull String url) {
		this.url = url;
	}

	@CheckForNull
	public Integer getNumberOfEntries() {
		return numberOfEntries;
	}
	public void setNumberOfEntries( @Nullable Integer numberOfEntries) {
		this.numberOfEntries = numberOfEntries;
	}




}
