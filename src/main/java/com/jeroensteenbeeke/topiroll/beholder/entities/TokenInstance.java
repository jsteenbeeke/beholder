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

import java.awt.Graphics2D;
import java.io.Serializable;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;

import com.jeroensteenbeeke.hyperion.data.BaseDomainObject;

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

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private TokenBorderIntensity borderIntensity;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "map")

	private ScaledMap map;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private TokenBorderType borderType;

	@Column(nullable = false)
	private int offsetY;

	@Column(nullable = false)
	private int offsetX;

	@Column(nullable = true)
	private String badge;

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

	@Nonnull
	public TokenBorderIntensity getBorderIntensity() {
		return borderIntensity;
	}

	public void setBorderIntensity(
			@Nonnull TokenBorderIntensity borderIntensity) {
		this.borderIntensity = borderIntensity;
	}

	@Nonnull
	public ScaledMap getMap() {
		return map;
	}

	public void setMap(@Nonnull ScaledMap map) {
		this.map = map;
	}

	public String calculateState() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");

		if (getBadge() != null) {
			sb.append("badge=");
			sb.append(getBadge());
			sb.append(";");
		}

		sb.append("border=");
		sb.append(getBorderType());
		sb.append(";intensity=");
		sb.append(getBorderIntensity());
		sb.append(";x=");
		sb.append(getOffsetX());
		sb.append(";y=");
		sb.append(getOffsetY());

		sb.append("{");

		return sb.toString();
	}

	public void drawPreviewTo(Graphics2D graphics2d) {
		// TODO
		
	}

}
