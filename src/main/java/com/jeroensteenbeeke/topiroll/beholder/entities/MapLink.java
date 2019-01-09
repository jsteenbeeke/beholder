package com.jeroensteenbeeke.topiroll.beholder.entities;

import java.io.Serializable;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;

import com.jeroensteenbeeke.hyperion.data.BaseDomainObject;
import com.jeroensteenbeeke.hyperion.webcomponents.entitypage.DefaultFieldType;
import com.jeroensteenbeeke.hyperion.webcomponents.entitypage.annotation.EntityFormField;

import javax.annotation.Nonnull;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.FetchType;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

@Entity
public class MapLink extends BaseDomainObject {


	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_MapLink", name = "MapLink", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "MapLink", strategy = GenerationType.SEQUENCE)
	@Access(value = AccessType.PROPERTY)

	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "source_group_id")
	private FogOfWarGroup sourceGroup;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "target_group_id")
	private FogOfWarGroup targetGroup;

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
	public FogOfWarGroup getSourceGroup() {
		return sourceGroup;
	}

	public void setSourceGroup(@Nonnull FogOfWarGroup sourceGroup) {
		this.sourceGroup = sourceGroup;
	}

	@Nonnull
	public FogOfWarGroup getTargetGroup() {
		return targetGroup;
	}

	public void setTargetGroup(@Nonnull FogOfWarGroup targetGroup) {
		this.targetGroup = targetGroup;
	}
}
