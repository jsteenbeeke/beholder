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
public class InitiativeParticipant extends BaseDomainObject {



	private static final long serialVersionUID = 1L;
 	@Id 	@SequenceGenerator(sequenceName="SEQ_ID_InitiativeParticipant", name="InitiativeParticipant", initialValue=1, allocationSize=1)
 	@GeneratedValue(generator="InitiativeParticipant", strategy=GenerationType.SEQUENCE)
 	@Access(value=AccessType.PROPERTY)

	private Long id;
 	@Column(nullable=true)
	private Integer total;


 	@Column(nullable=false)
	private int score;


 	@Column(nullable=false)
	private String name;


 	@ManyToOne(fetch=FetchType.LAZY, optional=false) 	@JoinColumn(name="view")

	private MapView view;






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
	public MapView getView() {
		return view;
	}
	public void setView( @Nonnull MapView view) {
		this.view = view;
	}

	@Nonnull
	public String getName() {
		return name;
	}
	public void setName( @Nonnull String name) {
		this.name = name;
	}

	@Nonnull
	public int getScore() {
		return score;
	}
	public void setScore( @Nonnull int score) {
		this.score = score;
	}

	@CheckForNull
	public Integer getTotal() {
		return total;
	}
	public void setTotal( @Nullable Integer total) {
		this.total = total;
	}











}
