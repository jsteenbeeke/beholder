package com.jeroensteenbeeke.topiroll.beholder.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.persistence.*;

import com.jeroensteenbeeke.hyperion.data.BaseDomainObject;

@Entity
public class FogOfWarGroup extends BaseDomainObject {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_FogOfWarGroup",
			name = "FogOfWarGroup", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "FogOfWarGroup",
			strategy = GenerationType.SEQUENCE)
	@Access(value = AccessType.PROPERTY)
	private Long id;
 	@ManyToOne(fetch=FetchType.LAZY, optional=false) 	@JoinColumn(name="map")

	private ScaledMap map;
 	@Column(nullable=false)
	private String name;





	@OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
	private List<FogOfWarShape> shapes = new ArrayList<FogOfWarShape>();

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
	public List<FogOfWarShape> getShapes() {
		return shapes;
	}

	public void setShapes(@Nonnull List<FogOfWarShape> shapes) {
		this.shapes = shapes;
	}

	@Nonnull
	public ScaledMap getMap() {
		return map;
	}
	public void setMap( @Nonnull ScaledMap map) {
		this.map = map;
	}

	@Nonnull
	public String getName() {
		return name;
	}
	public void setName( @Nonnull String name) {
		this.name = name;
	}





}
