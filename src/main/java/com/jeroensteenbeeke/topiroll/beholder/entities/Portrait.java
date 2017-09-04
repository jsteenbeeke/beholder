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
import java.util.List;
import java.util.ArrayList;
import javax.persistence.OneToMany;

	@Entity 
public class Portrait extends BaseDomainObject {



	private static final long serialVersionUID = 1L;
 	@Id 	@SequenceGenerator(sequenceName="SEQ_ID_Portrait", name="Portrait", initialValue=1, allocationSize=1)
 	@GeneratedValue(generator="Portrait", strategy=GenerationType.SEQUENCE)
 	@Access(value=AccessType.PROPERTY)

	private Long id;
 	@OneToMany(mappedBy="portrait", fetch=FetchType.LAZY)
	private List<PortraitVisibility> visibilities = new ArrayList<PortraitVisibility>();


 	@Column(nullable=false)
	private String name;


 	@Column(nullable=false)
	private byte[] data;


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
	public BeholderUser getOwner() {
		return owner;
	}
	public void setOwner( @Nonnull BeholderUser owner) {
		this.owner = owner;
	}

	@Nonnull
	public byte[] getData() {
		return data;
	}
	public void setData( @Nonnull byte[] data) {
		this.data = data;
	}

	@Nonnull
	public String getName() {
		return name;
	}
	public void setName( @Nonnull String name) {
		this.name = name;
	}

	@Nonnull
	public List<PortraitVisibility> getVisibilities() {
		return visibilities;
	}
	public void setVisibilities( @Nonnull List<PortraitVisibility> visibilities) {
		this.visibilities = visibilities;
	}











}
