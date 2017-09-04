package com.jeroensteenbeeke.topiroll.beholder.entities;

import java.io.Serializable;
import javax.persistence.*;

import com.jeroensteenbeeke.hyperion.data.BaseDomainObject;

import javax.annotation.Nonnull;

@Entity
public class PortraitVisibility extends BaseDomainObject {


	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_PortraitVisibility", name = "PortraitVisibility", initialValue = 1,
			allocationSize = 1)
	@GeneratedValue(generator = "PortraitVisibility", strategy = GenerationType.SEQUENCE)
	@Access(value = AccessType.PROPERTY)

	private Long id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "portrait")

	private Portrait portrait;
 	@Column(nullable=false)
	@Enumerated(EnumType.STRING)
	private PortraitVisibilityLocation location;


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
	public Portrait getPortrait() {
		return portrait;
	}

	public void setPortrait(@Nonnull Portrait portrait) {
		this.portrait = portrait;
	}

	@Nonnull
	public MapView getView() {
		return view;
	}

	public void setView(@Nonnull MapView view) {
		this.view = view;
	}

	@Nonnull
	public PortraitVisibilityLocation getLocation() {
		return location;
	}

	public void setLocation( @Nonnull PortraitVisibilityLocation location) {
		this.location = location;
	}




}
