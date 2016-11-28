package com.jeroensteenbeeke.topiroll.beholder.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.Serializable;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;

import com.jeroensteenbeeke.hyperion.data.BaseDomainObject;
import com.jeroensteenbeeke.topiroll.beholder.util.JSBuilder;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class FogOfWarShape extends BaseDomainObject implements ICanHazVisibilityStatus {

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
	@JoinColumn(name = "groupId")
	private FogOfWarGroup group;
	
	@Column(nullable=false)
	@Enumerated(EnumType.STRING)
	private VisibilityStatus status = VisibilityStatus.INVISIBLE;

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
	
	@Nonnull
	public VisibilityStatus getStatus() {
		return status;
	}
	
	public void setStatus(@Nonnull VisibilityStatus status) {
		this.status = status;
	}

	@Transient
	public abstract String getDescription();
	
	public abstract void drawPreviewTo(@Nonnull Graphics2D graphics2d);

	public abstract void renderTo(JSBuilder builder, String contextVariable, double multiplier, 
			boolean previewMode);
	
	public boolean shouldRender(boolean previewMode) {
		FogOfWarGroup _group = getGroup();
		
		return getStatus().isVisible(previewMode) || (_group != null && _group.getStatus().isVisible(previewMode));
	}

	protected final int rel(int input, double multiplier) {
		return (int) (input * multiplier);
	}
}
