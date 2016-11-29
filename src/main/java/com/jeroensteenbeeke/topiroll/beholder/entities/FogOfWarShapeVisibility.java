package com.jeroensteenbeeke.topiroll.beholder.entities;

import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class FogOfWarShapeVisibility extends FogOfWarVisibility {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "shape")
	private FogOfWarShape shape;

	@Nonnull
	public FogOfWarShape getShape() {
		return shape;
	}

	public void setShape(@Nonnull FogOfWarShape shape) {
		this.shape = shape;
	}

}
