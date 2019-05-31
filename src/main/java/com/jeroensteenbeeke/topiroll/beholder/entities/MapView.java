/**
 * This file is part of Beholder
 * (C) 2016 Jeroen Steenbeeke
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jeroensteenbeeke.topiroll.beholder.entities;

import com.jeroensteenbeeke.hyperion.data.BaseDomainObject;
import com.jeroensteenbeeke.hyperion.webcomponents.entitypage.DefaultFieldType;
import com.jeroensteenbeeke.hyperion.webcomponents.entitypage.annotation.EntityFormField;
import com.jeroensteenbeeke.hyperion.webcomponents.entitypage.annotation.Minimum;
import com.jeroensteenbeeke.topiroll.beholder.web.data.InitiativeRenderable;
import com.jeroensteenbeeke.topiroll.beholder.web.data.JSRenderable;
import org.danekja.java.misc.serializable.SerializableComparator;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class MapView extends BaseDomainObject {

	private static final long serialVersionUID = 1L;

	private static final Dimension DEFAULT_DIMENSION = new Dimension(320, 240);

	private static final int DEFAULT_MARGIN = 5;

	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_MapView", name = "MapView", initialValue = 1, allocationSize = 1)
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

	@Column(nullable = false)
	@EntityFormField(label = "Listen to doorbell", required = true, type = DefaultFieldType.CheckBox.class)
	private boolean listenToDoorbell;

	@OneToMany(mappedBy = "view", fetch = FetchType.LAZY)
	private List<PortraitVisibility> portraitVisibilities = new ArrayList<PortraitVisibility>();

	@Column(nullable = true)
	private Integer initiativeMargin;

	@Column(nullable = true)
	private InitiativeLocation initiativePosition;

	@OneToMany(mappedBy = "view", fetch = FetchType.LAZY)
	private List<InitiativeParticipant> initiativeParticipants = new ArrayList<InitiativeParticipant>();

	@OneToMany(mappedBy = "view", fetch = FetchType.LAZY)
	private List<AreaMarker> markers = new ArrayList<AreaMarker>();

	@OneToMany(mappedBy = "view", fetch = FetchType.LAZY)
	private List<FogOfWarVisibility> visibilities = new ArrayList<FogOfWarVisibility>();

	@Column(nullable = false)
	@Version
	private long version;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "selectedMap")
	private ScaledMap selectedMap;

	@Column(nullable = false)
	@EntityFormField(label = "Width", required = true)
	@Minimum(640)
	private int width;

	@Column(nullable = false)
	@EntityFormField(label = "Screen Diagonal", required = true)
	@Minimum(7)
	private int screenDiagonalInInches;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "owner")

	private BeholderUser owner;

	public static final SerializableComparator<InitiativeParticipant> INITIATIVE_ORDER = (a, b) -> {
		int total_a = a.getTotal() != null ? a.getTotal() : Integer.MIN_VALUE;
		int total_b = b.getTotal() != null ? b.getTotal() : Integer.MIN_VALUE;

		int c = Integer.compare(total_b, total_a);

		if (c == 0) {
			c = Integer.compare(b.getScore(), a.getScore());
		}

		if (c == 0 && a.getOrderOverride() != null
			&& b.getOrderOverride() != null) {
			c = Integer.compare(a.getOrderOverride(), b.getOrderOverride());
		}

		if (c == 0) {
			c = b.getName().compareTo(a.getName());
		}

		return c;

	};
	;

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

	public void setSelectedMap(@Nullable ScaledMap selectedMap) {
		this.selectedMap = selectedMap;
	}

	@Nonnull
	public long getVersion() {
		return version;
	}

	public void setVersion(@Nonnull long version) {
		this.version = version;
	}

	public Dimension toResolution() {
		return new Dimension(getWidth(), getHeight());
	}

	@Nonnull
	public List<FogOfWarVisibility> getVisibilities() {
		return visibilities;
	}

	public void setVisibilities(
		@Nonnull List<FogOfWarVisibility> visibilities) {
		this.visibilities = visibilities;
	}

	@Transient
	public Dimension getPreviewDimensions() {
		if (getSelectedMap() == null) {
			return DEFAULT_DIMENSION;
		}

		return getSelectedMap().getPreviewDimension();
	}

	@Nonnull
	public List<AreaMarker> getMarkers() {
		return markers;
	}

	public void setMarkers(@Nonnull List<AreaMarker> markers) {
		this.markers = markers;
	}

	@Nonnull
	public List<InitiativeParticipant> getInitiativeParticipants() {
		return initiativeParticipants;
	}

	public void setInitiativeParticipants(
		@Nonnull List<InitiativeParticipant> initiativeParticipants) {
		this.initiativeParticipants = initiativeParticipants;
	}

	@CheckForNull
	public InitiativeLocation getInitiativePosition() {
		return initiativePosition;
	}

	public void setInitiativePosition(
		@Nullable InitiativeLocation initiativePosition) {
		this.initiativePosition = initiativePosition;
	}

	public JSRenderable getInitiativeJS() {
		InitiativeRenderable renderable = new InitiativeRenderable();

		InitiativeLocation pos = getInitiativePosition();
		Integer margin = getInitiativeMargin();
		renderable.setShow(pos != null);
		renderable.setPosition(pos != null ? pos.toJS() : null);
		renderable.setMargin(margin != null ? margin : DEFAULT_MARGIN);

		renderable.setParticipants(
			getInitiativeParticipants().stream().sorted(INITIATIVE_ORDER)
				.map(InitiativeParticipant::toJS).collect(Collectors.toList()));

		return renderable;
	}

	@CheckForNull
	public Integer getInitiativeMargin() {
		return initiativeMargin;
	}

	public void setInitiativeMargin(@Nullable Integer initiativeMargin) {
		this.initiativeMargin = initiativeMargin;
	}

	@Nonnull
	public List<PortraitVisibility> getPortraitVisibilities() {
		return portraitVisibilities;
	}

	public void setPortraitVisibilities(
		@Nonnull List<PortraitVisibility> portraitVisibilities) {
		this.portraitVisibilities = portraitVisibilities;
	}

	@Nonnull
	public boolean isListenToDoorbell() {
		return listenToDoorbell;
	}

	public void setListenToDoorbell(@Nonnull boolean listenToDoorbell) {
		this.listenToDoorbell = listenToDoorbell;
	}

}
