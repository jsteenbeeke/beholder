package com.jeroensteenbeeke.topiroll.beholder.entities;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.persistence.*;

import com.jeroensteenbeeke.hyperion.data.BaseDomainObject;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class FogOfWarVisibility extends BaseDomainObject {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_FogOfWarVisibility",
			name = "FogOfWarVisibility", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "FogOfWarVisibility",
			strategy = GenerationType.SEQUENCE)
	@Access(value = AccessType.PROPERTY)

	private Long id;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private VisibilityStatus status;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "view")

	private MapView view;

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
	public MapView getView() {
		return view;
	}

	public void setView(@Nonnull MapView view) {
		this.view = view;
	}

	@Nonnull
	public VisibilityStatus getStatus() {
		return status;
	}

	public void setStatus(@Nonnull VisibilityStatus status) {
		this.status = status;
	}

}
