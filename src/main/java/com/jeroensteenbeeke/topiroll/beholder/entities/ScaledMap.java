package com.jeroensteenbeeke.topiroll.beholder.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.persistence.*;

import com.jeroensteenbeeke.hyperion.data.BaseDomainObject;

@Entity
public class ScaledMap extends BaseDomainObject {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_ScaledMap", name = "ScaledMap",
			initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "ScaledMap", strategy = GenerationType.SEQUENCE)
	@Access(value = AccessType.PROPERTY)

	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private int squareSize;

	@Column(nullable = false)
	private byte[] data;

	@OneToMany(mappedBy = "selectedMap", fetch = FetchType.LAZY)
	private List<MapView> selectedBy = new ArrayList<MapView>();

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
	public List<MapView> getSelectedBy() {
		return selectedBy;
	}

	public void setSelectedBy(@Nonnull List<MapView> selectedBy) {
		this.selectedBy = selectedBy;
	}

	@Nonnull
	public byte[] getData() {
		return data;
	}

	public void setData(@Nonnull byte[] data) {
		this.data = data;
	}

	@Nonnull
	public int getSquareSize() {
		return squareSize;
	}

	public void setSquareSize(@Nonnull int squareSize) {
		this.squareSize = squareSize;
	}

	@Nonnull
	public String getName() {
		return name;
	}

	public void setName(@Nonnull String name) {
		this.name = name;
	}

}
