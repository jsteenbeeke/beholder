package com.jeroensteenbeeke.topiroll.beholder.entities;

import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class FogOfWarGroupVisibility extends FogOfWarVisibility {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "group_id")

	private FogOfWarGroup group;

	@Nonnull
	public FogOfWarGroup getGroup() {
		return group;
	}

	public void setGroup(@Nonnull FogOfWarGroup group) {
		this.group = group;
	}

}
