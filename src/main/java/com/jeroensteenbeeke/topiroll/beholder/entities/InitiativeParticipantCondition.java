package com.jeroensteenbeeke.topiroll.beholder.entities;

import com.jeroensteenbeeke.hyperion.data.BaseDomainObject;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import java.io.Serializable;

@Entity
public class InitiativeParticipantCondition extends BaseDomainObject {

	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_InitiativeParticipantCondition", name = "InitiativeParticipantCondition", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "InitiativeParticipantCondition", strategy = GenerationType.SEQUENCE)
	@Access(value = AccessType.PROPERTY)

	private Long id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "participant")

	private InitiativeParticipant participant;
	@Column(nullable = true)
	private Integer turnsRemaining;

	@Column(nullable = false)
	private String description;

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
	public InitiativeParticipant getParticipant() {
		return participant;
	}

	public void setParticipant(@Nonnull InitiativeParticipant participant) {
		this.participant = participant;
	}

	@Nonnull
	public String getDescription() {
		return description;
	}

	public void setDescription(@Nonnull String description) {
		this.description = description;
	}

	@CheckForNull
	public Integer getTurnsRemaining() {
		return turnsRemaining;
	}

	public void setTurnsRemaining(@Nullable Integer turnsRemaining) {
		this.turnsRemaining = turnsRemaining;
	}

}
