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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import java.io.Serializable;

@Entity
public class TokenInstance extends BaseDomainObject {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_TokenInstance",
			name = "TokenInstance", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "TokenInstance",
			strategy = GenerationType.SEQUENCE)
	@Access(value = AccessType.PROPERTY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "map")
	private ScaledMap map;

	@Column(nullable = true)
	private String note;

	@Column(nullable = true)
	private Integer maxHitpoints;


	@Column(nullable = true)
	private Integer currentHitpoints;


	@Column(nullable = false)
	private boolean show;

	@Column(nullable = true)
	@EntityFormField(label = "Badge", required = true)
	private String badge;


	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	@EntityFormField(label = "Border", required = true, type = DefaultFieldType.DropDownChoice.class)
	private TokenBorderType borderType;

	@Column(nullable = false)
	private int offsetY;

	@Column(nullable = false)
	private int offsetX;


	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "definition")
	private TokenDefinition definition;

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
	public TokenDefinition getDefinition() {
		return definition;
	}

	public void setDefinition(@Nonnull TokenDefinition definition) {
		this.definition = definition;
	}

	@CheckForNull
	public String getBadge() {
		return badge;
	}

	public void setBadge(@Nullable String badge) {
		this.badge = badge;
	}

	@Nonnull
	public int getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(@Nonnull int offsetX) {
		this.offsetX = offsetX;
	}

	@Nonnull
	public int getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(@Nonnull int offsetY) {
		this.offsetY = offsetY;
	}

	@Nonnull
	public TokenBorderType getBorderType() {
		return borderType;
	}

	public void setBorderType(@Nonnull TokenBorderType borderType) {
		this.borderType = borderType;
	}

	@Transient
	public TokenBorderIntensity getBorderIntensity() {
		Integer curHP = getCurrentHitpoints();
		Integer maxHP = getMaxHitpoints();

		if (curHP != null && maxHP != null) {
			int c = Math.max(0, curHP.intValue());
			int m = maxHP.intValue();

			if (c >= 0 && m > 0 && c <= m) {
				int pc = 100 * c / m;

				if (pc == 0) {
					return TokenBorderIntensity.DEAD;
				} else if (pc < 25) {
					return TokenBorderIntensity.HEAVILY_INJURED;
				} else if (pc < 70) {
					return TokenBorderIntensity.MODERATELY_INJURED;
				} else if (pc < 90) {
					return TokenBorderIntensity.MINOR_INJURIES;
				}
			}
		}

		return TokenBorderIntensity.HEALTHY;
	}

	@Nonnull
	public ScaledMap getMap() {
		return map;
	}

	public void setMap(@Nonnull ScaledMap map) {
		this.map = map;
	}

	@Transient
	public boolean isVisible(@Nonnull MapView view, boolean previewMode) {
		return view.getVisibilities().stream()
				.anyMatch(v -> v.getStatus().isVisible(previewMode)
						&& v.containsCoordinate(getOffsetX(), getOffsetY()));
	}


	@Transient
	public String getLabel() {
		String label = getBadge();

		if (label == null) {
			return String.format("Unlabeled %s #%d",
					getDefinition().getName(),
					getId());
		}

		return label;
	}

	@Nonnull
	public boolean isShow() {
		return show;
	}

	public void setShow(@Nonnull boolean show) {
		this.show = show;
	}

	@CheckForNull
	public Integer getCurrentHitpoints() {
		return currentHitpoints;
	}

	public void setCurrentHitpoints(@Nullable Integer currentHitpoints) {
		this.currentHitpoints = currentHitpoints;
	}

	@CheckForNull
	public Integer getMaxHitpoints() {
		return maxHitpoints;
	}

	public void setMaxHitpoints(@Nullable Integer maxHitpoints) {
		this.maxHitpoints = maxHitpoints;
	}

	@CheckForNull
	public String getNote() {
		return note;
	}

	public void setNote(@Nullable String note) {
		this.note = note;
	}

	public String toPreview(double factor) {
		return String.format("{\n" +
						"\t'src': '%s',\n" +
						"\t'border_type': '%s',\n" +
						"\t'x': %d,\n" +
						"\t'y': %d,\n" +
						"\t'width': %d,\n" +
						"\t'height': %d,\n" +
						"}",
				getDefinition().getImageUrl(),
				getBorderType().name(), (int) (getOffsetX() * factor), (int) (getOffsetY() * factor),
				(int) (getMap().getSquareSize() * factor * getDefinition().getDiameterInSquares()),
				(int) (getMap().getSquareSize() * factor * getDefinition().getDiameterInSquares()));
	}


}
