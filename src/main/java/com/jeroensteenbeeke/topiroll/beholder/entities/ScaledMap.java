/**
 * This file is part of Beholder (C) 2016 Jeroen Steenbeeke
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.jeroensteenbeeke.topiroll.beholder.entities;

import java.awt.Dimension;
import java.io.Serializable;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.persistence.*;

import com.jeroensteenbeeke.hyperion.data.BaseDomainObject;
import com.jeroensteenbeeke.topiroll.beholder.util.Calculations;
import org.apache.wicket.model.IModel;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

@Entity
public class ScaledMap extends BaseDomainObject implements AmazonStored {

	private static final long serialVersionUID = 1L;

	private static final int MAX_PREVIEW_SIZE = 640;

	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_ScaledMap", name = "ScaledMap",
			initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "ScaledMap", strategy = GenerationType.SEQUENCE)
	@Access(value = AccessType.PROPERTY)

	private Long id;
 	@OneToMany(mappedBy="map", fetch=FetchType.LAZY)
	private List<MapLink> incomingLinks = new ArrayList<MapLink>();


 	@Column(nullable=true, name="amazon_key")
	private String amazonKey;


	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "folder")
	private MapFolder folder;


	@OneToMany(mappedBy = "map", fetch = FetchType.LAZY)
	private List<TokenInstance> tokens = new ArrayList<>();


	@Column(nullable = false)
	private int basicHeight;

	@Column(nullable = false)
	private int basicWidth;

	@OneToMany(mappedBy = "map", fetch = FetchType.LAZY)
	private List<FogOfWarGroup> groups = new ArrayList<>();

	@OneToMany(mappedBy = "map", fetch = FetchType.LAZY)
	private List<FogOfWarShape> fogOfWarShapes = new ArrayList<>();

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "owner")
	private BeholderUser owner;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private int squareSize;

	@Column(nullable = true)
	@Lob
	private Blob data;

	@OneToMany(mappedBy = "selectedMap", fetch = FetchType.LAZY)
	private List<MapView> selectedBy = new ArrayList<>();

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
	public Blob getData() {
		return data;
	}

	public void setData(@Nonnull Blob data) {
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

	@Nonnull
	public List<TokenInstance> getTokens() {
		return tokens;
	}

	public void setTokens(@Nonnull List<TokenInstance> tokens) {
		this.tokens = tokens;
	}

	@CheckForNull
	public MapFolder getFolder() {
		return folder;
	}

	public void setFolder(@Nullable MapFolder folder) {
		this.folder = folder;
	}


	public List<FogOfWarShape> getAllShapes() {
		Map<Long, FogOfWarShape> shapes = getFogOfWarShapes().stream()
				.collect(Collectors.toMap(FogOfWarShape::getId,
						Function.identity()));
		getGroups().stream().flatMap(g -> g.getShapes().stream())
				.filter(s -> !shapes.containsKey(s.getId())).forEach(s -> shapes.put(s.getId(), s));
		return new ArrayList<>(shapes.values());
	}

	@CheckForNull
	@Override
	public String getAmazonKey() {
		return amazonKey;
	}
	public void setAmazonKey( @Nullable String amazonKey) {
		this.amazonKey = amazonKey;
	}

	@Nonnull
	public List<MapLink> getIncomingLinks() {
		return incomingLinks;
	}
	public void setIncomingLinks( @Nonnull List<MapLink> incomingLinks) {
		this.incomingLinks = incomingLinks;
	}


	public String getNameWithFolders() {
		if (folder != null) {
			return String.format("%s \\ %s", folder.getNameWithParents(), getName());
		}

		return getName();
	}
}
