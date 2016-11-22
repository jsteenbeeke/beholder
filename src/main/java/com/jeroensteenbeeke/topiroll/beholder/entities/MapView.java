package com.jeroensteenbeeke.topiroll.beholder.entities;

import java.io.Serializable;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;

import com.jeroensteenbeeke.hyperion.data.BaseDomainObject;
import com.jeroensteenbeeke.hyperion.ducktape.web.pages.entity.annotation.EntityFormField;
import com.jeroensteenbeeke.hyperion.ducktape.web.pages.entity.annotation.Minimum;
import com.jeroensteenbeeke.hyperion.util.HashUtil;
import com.jeroensteenbeeke.topiroll.beholder.util.Resolution;
import com.jeroensteenbeeke.topiroll.beholder.util.SimpleResolution;

@Entity
public class MapView extends BaseDomainObject {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_MapView", name = "MapView",
			initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "MapView", strategy = GenerationType.SEQUENCE)
	@Access(value = AccessType.PROPERTY)

	private Long id;

	@Column(nullable = false)
	@EntityFormField(label = "Identifier", required = true)
	private String identifier;

	@Column(nullable = false)
	@EntityFormField(label = "Height", required = true)
	@Minimum(480)
	private int height;
	
 	@Column(nullable=false)
 	@Version
	private long version;


 	@ManyToOne(fetch=FetchType.LAZY, optional=true) 	@JoinColumn(name="selectedMap")

	private ScaledMap selectedMap;



	@Column(nullable = false)
	@EntityFormField(label = "Width", required = true)
	@Minimum(640)
	private int width;

	@Column(nullable = false)
	@EntityFormField(label = "Screen Diagonal", required = true)
	@Minimum(24)
	private int screenDiagonalInInches;

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
	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(@Nonnull String identifier) {
		this.identifier = identifier;
	}

	@Nonnull
	public int getWidth() {
		return width;
	}

	public void setWidth(@Nonnull int width) {
		this.width = width;
	}

	@Nonnull
	public int getHeight() {
		return height;
	}

	public void setHeight(@Nonnull int height) {
		this.height = height;
	}

	@Nonnull
	public int getScreenDiagonalInInches() {
		return screenDiagonalInInches;
	}

	public void setScreenDiagonalInInches(@Nonnull int screenDiagonalInInches) {
		this.screenDiagonalInInches = screenDiagonalInInches;
	}

	@CheckForNull
	public ScaledMap getSelectedMap() {
		return selectedMap;
	}
	public void setSelectedMap( @Nullable ScaledMap selectedMap) {
		this.selectedMap = selectedMap;
	}

	@Nonnull
	public long getVersion() {
		return version;
	}
	public void setVersion( @Nonnull long version) {
		this.version = version;
	}

	public Resolution toResolution() {
		return new SimpleResolution(getWidth(), getHeight());
	}

	public String calculateState() {
		StringBuilder data = new StringBuilder();
		
		if (selectedMap != null) {
			data.append(selectedMap.getName());
		}
		
		return HashUtil.sha512Hash(data.toString());
	}





}
