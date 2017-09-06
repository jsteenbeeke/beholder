/**
 * This file is part of Beholder
 * (C) 2016 Jeroen Steenbeeke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jeroensteenbeeke.topiroll.beholder.entities;

import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.persistence.*;

import org.apache.wicket.model.IModel;

import com.jeroensteenbeeke.hyperion.data.BaseDomainObject;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.hyperion.util.ImageUtil;

@Entity
public class FogOfWarGroup extends BaseDomainObject
		implements ICanHazVisibilityStatus {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_FogOfWarGroup",
			name = "FogOfWarGroup", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "FogOfWarGroup",
			strategy = GenerationType.SEQUENCE)
	@Access(value = AccessType.PROPERTY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "map")

	private ScaledMap map;

	@OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
	private List<FogOfWarGroupVisibility> visibilities = new ArrayList<FogOfWarGroupVisibility>();

	@Column(nullable = false)
	private String name;

	@OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
	private List<FogOfWarShape> shapes = new ArrayList<FogOfWarShape>();

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
	public List<FogOfWarShape> getShapes() {
		return shapes;
	}

	public void setShapes(@Nonnull List<FogOfWarShape> shapes) {
		this.shapes = shapes;
	}

	@Nonnull
	public ScaledMap getMap() {
		return map;
	}

	public void setMap(@Nonnull ScaledMap map) {
		this.map = map;
	}

	@Nonnull
	public String getName() {
		return name;
	}

	public void setName(@Nonnull String name) {
		this.name = name;
	}

	@Override
	public String getDescription() {
		return getName();
	}

	@Nonnull
	public List<FogOfWarGroupVisibility> getVisibilities() {
		return visibilities;
	}

	public void setVisibilities(
			@Nonnull List<FogOfWarGroupVisibility> visibilities) {
		this.visibilities = visibilities;
	}

	@Override
	public VisibilityStatus getStatus(MapView view) {

		return getVisibilities().stream().filter(v -> v.getView().equals(view))
				.findAny().map(FogOfWarGroupVisibility::getStatus)
				.orElse(VisibilityStatus.INVISIBLE);
	}

	public boolean containsCoordinate(int x, int y) {
		return getShapes().stream().anyMatch(shape -> shape.containsCoordinate(x, y));
	}

}
