/**
 * This file is part of Beholder
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jeroensteenbeeke.topiroll.beholder.entities;

import java.awt.Dimension;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;

import com.jeroensteenbeeke.hyperion.data.BaseDomainObject;
import com.jeroensteenbeeke.hyperion.ducktape.web.pages.entity.annotation.EntityFormField;
import com.jeroensteenbeeke.hyperion.ducktape.web.pages.entity.annotation.Minimum;
import com.jeroensteenbeeke.hyperion.util.HashUtil;

@Entity
public class MapView extends BaseDomainObject {

	private static final long serialVersionUID = 1L;

	private static final Dimension DEFAULT_DIMENSION = new Dimension(320, 240);

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
 	@OneToMany(mappedBy="view", fetch=FetchType.LAZY)
	private List<TokenInstance> tokens = new ArrayList<TokenInstance>();



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

	public String calculateState() {
		StringBuilder data = new StringBuilder();

		if (selectedMap != null) {
			data.append(selectedMap.getName());

			getVisibilities().stream()
					.sorted(Comparator.comparing(FogOfWarVisibility::getId))
					.forEach(s -> {
						
							data.append(";V");
							data.append(s.getId());
							data.append("=");
							data.append(s.getStatus().toString());

					});
			getTokens().stream().sorted(Comparator.comparing(TokenInstance::getId)).forEach(t -> {
				data.append(";T");
				data.append(t.getId());
			});
		}

		return HashUtil.sha512Hash(data.toString());
	}

	@Nonnull
	public List<FogOfWarVisibility> getVisibilities() {
		return visibilities;
	}

	public void setVisibilities(
			@Nonnull List<FogOfWarVisibility> visibilities) {
		this.visibilities = visibilities;
	}

	@Nonnull
	public List<TokenInstance> getTokens() {
		return tokens;
	}
	public void setTokens( @Nonnull List<TokenInstance> tokens) {
		this.tokens = tokens;
	}

	@Transient
	public Dimension getPreviewDimensions() {
		if (getSelectedMap() == null) {
			return DEFAULT_DIMENSION;
		}
		
		return getSelectedMap().getPreviewDimension();
	}

}
