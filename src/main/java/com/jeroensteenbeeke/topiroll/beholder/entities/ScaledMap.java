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
import javax.persistence.Column;

	@Entity 
public class ScaledMap extends BaseDomainObject {



	private static final long serialVersionUID = 1L;
 	@Id 	@SequenceGenerator(sequenceName="SEQ_ID_ScaledMap", name="ScaledMap", initialValue=1, allocationSize=1)
 	@GeneratedValue(generator="ScaledMap", strategy=GenerationType.SEQUENCE)
 	@Access(value=AccessType.PROPERTY)

	private Long id;
 	@Column(nullable=false)
	private int squareSize;


 	@Column(nullable=false)
	private byte[] data;


 	@OneToMany(mappedBy="selectedMap", fetch=FetchType.LAZY)
	private List<MapView> selectedBy = new ArrayList<MapView>();






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
	public List<MapView> getSelectedBy() {
		return selectedBy;
	}
	public void setSelectedBy( @Nonnull List<MapView> selectedBy) {
		this.selectedBy = selectedBy;
	}

	@Nonnull
	public byte[] getData() {
		return data;
	}
	public void setData( @Nonnull byte[] data) {
		this.data = data;
	}

	@Nonnull
	public int getSquareSize() {
		return squareSize;
	}
	public void setSquareSize( @Nonnull int squareSize) {
		this.squareSize = squareSize;
	}









}
