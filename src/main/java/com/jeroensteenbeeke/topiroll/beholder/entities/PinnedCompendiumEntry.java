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

import javax.annotation.Nonnull;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.FetchType;

@Entity
public class PinnedCompendiumEntry extends BaseDomainObject {


	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_PinnedCompendiumEntry", name = "PinnedCompendiumEntry", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "PinnedCompendiumEntry", strategy = GenerationType.SEQUENCE)
	@Access(value = AccessType.PROPERTY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "pinnedBy")
	private BeholderUser pinnedBy;


	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "entry")
	private CompendiumEntry entry;

	protected PinnedCompendiumEntry() {
	}

	public PinnedCompendiumEntry(BeholderUser pinnedBy, CompendiumEntry entry) {
		this.pinnedBy = pinnedBy;
		this.entry = entry;
	}

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
	public CompendiumEntry getEntry() {
		return entry;
	}

	public void setEntry(@Nonnull CompendiumEntry entry) {
		this.entry = entry;
	}

	@Nonnull
	public BeholderUser getPinnedBy() {
		return pinnedBy;
	}

	public void setPinnedBy(@Nonnull BeholderUser pinnedBy) {
		this.pinnedBy = pinnedBy;
	}


}
