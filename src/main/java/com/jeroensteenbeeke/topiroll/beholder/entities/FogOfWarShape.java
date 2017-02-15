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

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;

import com.jeroensteenbeeke.hyperion.data.BaseDomainObject;
import com.jeroensteenbeeke.topiroll.beholder.util.JSBuilder;
import com.jeroensteenbeeke.topiroll.beholder.web.data.shapes.JSShape;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class FogOfWarShape extends BaseDomainObject
		implements ICanHazVisibilityStatus {

	private static final long serialVersionUID = 1L;

	protected static final Color TRANSPARENT_BLUE = new Color(0f, 0f, 1f, 0.5f);

	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_FogOfWarShape",
			name = "FogOfWarShape", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "FogOfWarShape",
			strategy = GenerationType.SEQUENCE)
	@Access(value = AccessType.PROPERTY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "map")
	private ScaledMap map;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "group_id")
	private FogOfWarGroup group;

	@OneToMany(mappedBy = "shape", fetch = FetchType.LAZY)
	private List<FogOfWarShapeVisibility> visibilities = new ArrayList<FogOfWarShapeVisibility>();

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
	public ScaledMap getMap() {
		return map;
	}

	public void setMap(@Nonnull ScaledMap map) {
		this.map = map;
	}

	@CheckForNull
	public FogOfWarGroup getGroup() {
		return group;
	}

	public void setGroup(@Nullable FogOfWarGroup group) {
		this.group = group;
	}

	@Transient
	public abstract String getDescription();

	public abstract void drawPreviewTo(@Nonnull Graphics2D graphics2d);

	public abstract void renderTo(@Nonnull JSRenderContext context);

	public boolean shouldRender(MapView view, boolean previewMode) {
		FogOfWarGroup _group = getGroup();

		return getStatus(view).isVisible(previewMode) || (_group != null
				&& _group.getStatus(view).isVisible(previewMode));
	}

	protected final int rel(int input, double multiplier) {
		return (int) (input * multiplier);
	}

	@Nonnull
	public List<FogOfWarShapeVisibility> getVisibilities() {
		return visibilities;
	}

	public void setVisibilities(
			@Nonnull List<FogOfWarShapeVisibility> visibilities) {
		this.visibilities = visibilities;
	}

	@Override
	public VisibilityStatus getStatus(MapView view) {
		return getVisibilities().stream().filter(v -> v.getView().equals(view))
				.findAny().map(FogOfWarShapeVisibility::getStatus)
				.orElse(VisibilityStatus.INVISIBLE);
	}

	public static class JSRenderContext {
		private final JSBuilder javaScriptBuilder;

		private final String contextVariable;

		private final double multiplier;

		private final boolean previewMode;

		private final MapView view;

		public JSRenderContext(JSBuilder javaScriptBuilder,
				String contextVariable, double multiplier, boolean previewMode,
				MapView view) {
			this.javaScriptBuilder = javaScriptBuilder;
			this.contextVariable = contextVariable;
			this.multiplier = multiplier;
			this.previewMode = previewMode;
			this.view = view;
		}

		public JSBuilder getJavaScriptBuilder() {
			return javaScriptBuilder;
		}

		public String getContextVariable() {
			return contextVariable;
		}

		public double getMultiplier() {
			return multiplier;
		}

		public boolean isPreviewMode() {
			return previewMode;
		}

		public MapView getView() {
			return view;
		}

	}

	public abstract boolean containsCoordinate(int x, int y);

	public abstract JSShape toJS(double factor);
}
