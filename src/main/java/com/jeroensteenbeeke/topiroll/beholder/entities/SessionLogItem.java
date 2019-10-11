package com.jeroensteenbeeke.topiroll.beholder.entities;

import java.io.Serializable;
import javax.persistence.*;

import com.jeroensteenbeeke.hyperion.data.BaseDomainObject;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;

@Entity
public class SessionLogItem extends BaseDomainObject {


	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_SessionLogItem", name = "SessionLogItem", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "SessionLogItem", strategy = GenerationType.SEQUENCE)
	@Access(value = AccessType.PROPERTY)

	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id")
	private BeholderUser user;
 	@Column(nullable=false)
	private boolean completed;


 	@ManyToOne(fetch=FetchType.LAZY, optional=false) 	@JoinColumn(name="logIndex")

	private SessionLogIndex logIndex;



	@Column(nullable = false)
	@Lob
	private String eventDescription;

	@Column(nullable = false)
	private LocalDateTime eventTime;


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
	public BeholderUser getUser() {
		return user;
	}

	public void setUser(@Nonnull BeholderUser user) {
		this.user = user;
	}

	@Nonnull
	public LocalDateTime getEventTime() {
		return eventTime;
	}

	public void setEventTime(@Nonnull LocalDateTime eventTime) {
		this.eventTime = eventTime;
	}

	@Nonnull
	public String getEventDescription() {
		return eventDescription;
	}

	public void setEventDescription(@Nonnull String eventDescription) {
		this.eventDescription = eventDescription;
	}

	@Nonnull
	public SessionLogIndex getLogIndex() {
		return logIndex;
	}
	public void setLogIndex( @Nonnull SessionLogIndex logIndex) {
		this.logIndex = logIndex;
	}

	@Nonnull
	public boolean isCompleted() {
		return completed;
	}
	public void setCompleted( @Nonnull boolean completed) {
		this.completed = completed;
	}






}
