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
import java.util.List;
import java.util.ArrayList;
import javax.persistence.OneToMany;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Column;
import java.time.LocalDate;

	@Entity 
public class SessionLogIndex extends BaseDomainObject {



	private static final long serialVersionUID = 1L;
 	@Id 	@SequenceGenerator(sequenceName="SEQ_ID_SessionLogIndex", name="SessionLogIndex", initialValue=1, allocationSize=1)
 	@GeneratedValue(generator="SessionLogIndex", strategy=GenerationType.SEQUENCE)
 	@Access(value=AccessType.PROPERTY)

	private Long id;
 	@OneToMany(mappedBy="logIndex", fetch=FetchType.LAZY)
	private List<SessionLogItem> items = new ArrayList<SessionLogItem>();
 	@Column(nullable=false, name = "sessionday")
	private LocalDate day;


 	@ManyToOne(fetch=FetchType.LAZY, optional=false) 	@JoinColumn(name="owner")

	private BeholderUser owner;








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
	public List<SessionLogItem> getItems() {
		return items;
	}
	public void setItems( @Nonnull List<SessionLogItem> items) {
		this.items = items;
	}

	@Nonnull
	public BeholderUser getOwner() {
		return owner;
	}
	public void setOwner( @Nonnull BeholderUser owner) {
		this.owner = owner;
	}

	@Nonnull
	public LocalDate getDay() {
		return day;
	}
	public void setDay( @Nonnull LocalDate day) {
		this.day = day;
	}









}
