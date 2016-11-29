package com.jeroensteenbeeke.topiroll.beholder.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.persistence.*;

import com.jeroensteenbeeke.hyperion.data.BaseDomainObject;

@Entity
public class TokenDefinition extends BaseDomainObject {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_TokenDefinition",
			name = "TokenDefinition", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "TokenDefinition",
			strategy = GenerationType.SEQUENCE)
	@Access(value = AccessType.PROPERTY)

	private Long id;
 	@Column(nullable=false)
	private String name;



	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private TokenSize size;

	@Column(nullable = false)
	private byte[] imageData;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "owner")

	private BeholderUser owner;

	@OneToMany(mappedBy = "definition", fetch = FetchType.LAZY)
	private List<TokenInstance> instances = new ArrayList<TokenInstance>();

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
	public List<TokenInstance> getInstances() {
		return instances;
	}

	public void setInstances(@Nonnull List<TokenInstance> instances) {
		this.instances = instances;
	}

	@Nonnull
	public BeholderUser getOwner() {
		return owner;
	}

	public void setOwner(@Nonnull BeholderUser owner) {
		this.owner = owner;
	}

	@Nonnull
	public byte[] getImageData() {
		return imageData;
	}

	public void setImageData(@Nonnull byte[] imageData) {
		this.imageData = imageData;
	}

	@Nonnull
	public TokenSize getSize() {
		return size;
	}

	public void setSize(@Nonnull TokenSize size) {
		this.size = size;
	}

	@Nonnull
	public String getName() {
		return name;
	}
	public void setName( @Nonnull String name) {
		this.name = name;
	}



}
