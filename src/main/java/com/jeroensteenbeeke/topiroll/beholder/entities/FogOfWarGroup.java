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

	@Entity 
public class FogOfWarGroup extends BaseDomainObject {



	private static final long serialVersionUID = 1L;
 	@Id 	@SequenceGenerator(sequenceName="SEQ_ID_FogOfWarGroup", name="FogOfWarGroup", initialValue=1, allocationSize=1)
 	@GeneratedValue(generator="FogOfWarGroup", strategy=GenerationType.SEQUENCE)
 	@Access(value=AccessType.PROPERTY)

	private Long id;
 	@OneToMany(mappedBy="group", fetch=FetchType.LAZY)
	private List<FogOfWarShape> shapes = new ArrayList<FogOfWarShape>();






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
	public List<FogOfWarShape> getShapes() {
		return shapes;
	}
	public void setShapes( @Nonnull List<FogOfWarShape> shapes) {
		this.shapes = shapes;
	}





}
