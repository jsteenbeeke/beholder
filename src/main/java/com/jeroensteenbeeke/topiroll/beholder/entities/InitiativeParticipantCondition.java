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
import javax.persistence.Column;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

	@Entity 
public class InitiativeParticipantCondition extends BaseDomainObject {



	private static final long serialVersionUID = 1L;
 	@Id 	@SequenceGenerator(sequenceName="SEQ_ID_InitiativeParticipantCondition", name="InitiativeParticipantCondition", initialValue=1, allocationSize=1)
 	@GeneratedValue(generator="InitiativeParticipantCondition", strategy=GenerationType.SEQUENCE)
 	@Access(value=AccessType.PROPERTY)

	private Long id;
 	@ManyToOne(fetch=FetchType.LAZY, optional=false) 	@JoinColumn(name="participant")

	private InitiativeParticipant participant;
 	@Column(nullable=true)
	private Integer turnsRemaining;


 	@Column(nullable=false)
	private String description;








	public Long getId() {
		return id;
	}
	public void setId( @Nonnull Long id) {
		this.id = id;
	}

	@Override
	public final  Serializable getDomainObjectId() {
		return getId();
	}

	@Nonnull
	public InitiativeParticipant getParticipant() {
		return participant;
	}
	public void setParticipant( @Nonnull InitiativeParticipant participant) {
		this.participant = participant;
	}

	@Nonnull
	public String getDescription() {
		return description;
	}
	public void setDescription( @Nonnull String description) {
		this.description = description;
	}

	@CheckForNull
	public Integer getTurnsRemaining() {
		return turnsRemaining;
	}
	public void setTurnsRemaining( @Nullable Integer turnsRemaining) {
		this.turnsRemaining = turnsRemaining;
	}









}
