package com.jeroensteenbeeke.topiroll.beholder.entities;

import java.io.Serializable;
import javax.persistence.*;

import com.jeroensteenbeeke.hyperion.data.BaseDomainObject;
import javax.annotation.Nonnull;
import java.util.List;
import java.util.ArrayList;

@Entity
public class CompendiumEntry extends BaseDomainObject {



	private static final long serialVersionUID = 1L;
 	@Id 	@SequenceGenerator(sequenceName="SEQ_ID_CompendiumEntry", name="CompendiumEntry", initialValue=1, allocationSize=1)
 	@GeneratedValue(generator="CompendiumEntry", strategy=GenerationType.SEQUENCE)
 	@Access(value=AccessType.PROPERTY)

	private Long id;
 	@Column(nullable=false)
	private String title;
 	@Column(nullable=false)
	@Lob
	private String body;
 	@OneToMany(mappedBy="entry", fetch=FetchType.LAZY)
	private List<PinnedCompendiumEntry> pinnedBy = new ArrayList<PinnedCompendiumEntry>();


 	@Column(nullable=false)
	private String originalPath;










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
	public String getTitle() {
		return title;
	}
	public void setTitle( @Nonnull String title) {
		this.title = title;
	}

	@Nonnull
	public String getBody() {
		return body;
	}
	public void setBody( @Nonnull String body) {
		this.body = body;
	}

	@Nonnull
	public String getOriginalPath() {
		return originalPath;
	}
	public void setOriginalPath( @Nonnull String originalPath) {
		this.originalPath = originalPath;
	}

	@Nonnull
	public List<PinnedCompendiumEntry> getPinnedBy() {
		return pinnedBy;
	}
	public void setPinnedBy( @Nonnull List<PinnedCompendiumEntry> pinnedBy) {
		this.pinnedBy = pinnedBy;
	}











}
