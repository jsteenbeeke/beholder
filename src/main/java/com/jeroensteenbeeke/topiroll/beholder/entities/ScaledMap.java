package com.jeroensteenbeeke.topiroll.beholder.entities;

import java.awt.Dimension;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.persistence.*;

import com.jeroensteenbeeke.hyperion.data.BaseDomainObject;
import com.jeroensteenbeeke.topiroll.beholder.util.Calculations;

@Entity
public class ScaledMap extends BaseDomainObject {

	private static final long serialVersionUID = 1L;

	private static final int MAX_PREVIEW_SIZE = 640;

	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_ScaledMap", name = "ScaledMap",
			initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "ScaledMap", strategy = GenerationType.SEQUENCE)
	@Access(value = AccessType.PROPERTY)

	private Long id;

	@Column(nullable = false)
	private int basicHeight;

	@Column(nullable = false)
	private int basicWidth;

	@OneToMany(mappedBy = "map", fetch = FetchType.LAZY)
	private List<FogOfWarGroup> groups = new ArrayList<FogOfWarGroup>();

	@OneToMany(mappedBy = "map", fetch = FetchType.LAZY)
	private List<FogOfWarShape> fogOfWarShapes = new ArrayList<FogOfWarShape>();

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "owner")

	private BeholderUser owner;

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

	@Nonnull
	public BeholderUser getOwner() {
		return owner;
	}

	public void setOwner(@Nonnull BeholderUser owner) {
		this.owner = owner;
	}

	@Nonnull
	public List<FogOfWarShape> getFogOfWarShapes() {
		return fogOfWarShapes;
	}

	public void setFogOfWarShapes(@Nonnull List<FogOfWarShape> fogOfWarShapes) {
		this.fogOfWarShapes = fogOfWarShapes;
	}

	@Nonnull
	public List<FogOfWarGroup> getGroups() {
		return groups;
	}

	public void setGroups(@Nonnull List<FogOfWarGroup> groups) {
		this.groups = groups;
	}

	@Transient
	public double getPreviewFactor() {
		int w = (int) getBasicWidth();
		double factor = 1.0;

		while (w > MAX_PREVIEW_SIZE) {
			w = (int) (w * 0.9);
			factor = 0.9 * factor;
		}

		return factor;
	}

	@Transient
	public Dimension getPreviewDimension() {

		return new Dimension((int) (getPreviewFactor() * getBasicWidth()),
				(int) (getPreviewFactor() * getBasicHeight()));
	}

	@Transient
	public Dimension getDisplayDimension(MapView mapView) {
		double factor = getDisplayFactor(mapView);

		return new Dimension((int) (getBasicWidth() * factor),
				(int) (getBasicHeight() * factor));
	}

	@Transient
	public double getDisplayFactor(MapView mapView) {
		return Calculations.scale(getSquareSize())
				.toResolution(mapView.toResolution())
				.onScreenWithDiagonalSize(mapView.getScreenDiagonalInInches());
	}
	
	

	@Nonnull
	public int getBasicWidth() {
		return basicWidth;
	}

	public void setBasicWidth(@Nonnull int basicWidth) {
		this.basicWidth = basicWidth;
	}

	@Nonnull
	public int getBasicHeight() {
		return basicHeight;
	}

	public void setBasicHeight(@Nonnull int basicHeight) {
		this.basicHeight = basicHeight;
	}

}
