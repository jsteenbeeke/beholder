package com.jeroensteenbeeke.topiroll.beholder.entities;

import com.jeroensteenbeeke.hyperion.data.BaseDomainObject;
import com.jeroensteenbeeke.hyperion.webcomponents.entitypage.DefaultFieldType;
import com.jeroensteenbeeke.hyperion.webcomponents.entitypage.annotation.EntityFormField;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import java.io.Serializable;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Portrait extends BaseDomainObject implements AmazonStored {

	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_Portrait", name = "Portrait", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "Portrait", strategy = GenerationType.SEQUENCE)
	@Access(value = AccessType.PROPERTY)
	private Long id;

	@Column(nullable = false)
	@EntityFormField(label = "Name", required = true)
	private String name;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "campaign")
	@EntityFormField(label = "Campaign", type = DefaultFieldType.DropDownChoice.class)
	private Campaign campaign;

	@Column(name = "amazon_key")
	private String amazonKey;

	@OneToMany(mappedBy = "portrait", fetch = FetchType.LAZY)
	private List<PortraitVisibility> visibilities = new ArrayList<PortraitVisibility>();

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "owner")
	private BeholderUser owner;

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
	public BeholderUser getOwner() {
		return owner;
	}

	public void setOwner(@Nonnull BeholderUser owner) {
		this.owner = owner;
	}

	@Nonnull
	public String getName() {
		return name;
	}

	public void setName(@Nonnull String name) {
		this.name = name;
	}

	@Nonnull
	public List<PortraitVisibility> getVisibilities() {
		return visibilities;
	}

	public void setVisibilities(
		@Nonnull List<PortraitVisibility> visibilities) {
		this.visibilities = visibilities;
	}

	@CheckForNull
	@Override
	public String getAmazonKey() {
		return amazonKey;
	}

	public void setAmazonKey(@Nullable String amazonKey) {
		this.amazonKey = amazonKey;
	}

	@CheckForNull
	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(@Nullable Campaign campaign) {
		this.campaign = campaign;
	}

}
