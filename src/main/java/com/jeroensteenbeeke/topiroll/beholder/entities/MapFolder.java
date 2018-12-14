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
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.ArrayList;
import javax.persistence.OneToMany;

@Entity
public class MapFolder extends BaseDomainObject {


	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_MapFolder", name = "MapFolder", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "MapFolder", strategy = GenerationType.SEQUENCE)
	@Access(value = AccessType.PROPERTY)

	private Long id;
	@OneToMany(mappedBy = "folder", fetch = FetchType.LAZY)
	private List<ScaledMap> maps = new ArrayList<ScaledMap>();


	@OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
	private List<MapFolder> children = new ArrayList<MapFolder>();


	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "parent")

	private MapFolder parent;


	@Column(nullable = false)
	@EntityFormField(label = "Name", required = true)
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

	@CheckForNull
	public MapFolder getParent() {
		return parent;
	}

	public void setParent(@Nullable MapFolder parent) {
		this.parent = parent;
	}

	@Nonnull
	public List<MapFolder> getChildren() {
		return children;
	}

	public void setChildren(@Nonnull List<MapFolder> children) {
		this.children = children;
	}

	@Nonnull
	public List<ScaledMap> getMaps() {
		return maps;
	}

	public void setMaps(@Nonnull List<ScaledMap> maps) {
		this.maps = maps;
	}


	public String getNameWithParents() {
		if (parent != null) {
			return String.format("%s \\ %s", parent.getNameWithParents(), getName());
		}

		return getName();
	}
}
