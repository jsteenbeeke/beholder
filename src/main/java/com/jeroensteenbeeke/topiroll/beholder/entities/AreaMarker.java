package com.jeroensteenbeeke.topiroll.beholder.entities;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.persistence.*;

import org.apache.wicket.markup.html.panel.Panel;

import com.jeroensteenbeeke.hyperion.data.BaseDomainObject;
import com.jeroensteenbeeke.topiroll.beholder.util.JSBuilder;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AreaMarker extends BaseDomainObject {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_AreaMarker", name = "AreaMarker",
			initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "AreaMarker",
			strategy = GenerationType.SEQUENCE)
	@Access(value = AccessType.PROPERTY)

	private Long id;
 	@Column(nullable=false)
	private String color;



	@Column(nullable = false)
	private int extent;

	@Column(nullable = false)
	private int offsetY;

	@Column(nullable = false)
	private int offsetX;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "view")

	private MapView view;

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
	public MapView getView() {
		return view;
	}

	public void setView(@Nonnull MapView view) {
		this.view = view;
	}

	@Nonnull
	public int getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(@Nonnull int offsetX) {
		this.offsetX = offsetX;
	}

	@Nonnull
	public int getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(@Nonnull int offsetY) {
		this.offsetY = offsetY;
	}

	@Nonnull
	public int getExtent() {
		return extent;
	}

	public void setExtent(@Nonnull int extent) {
		this.extent = extent;
	}

	public abstract void renderTo(String contextVariable, JSBuilder js,
			double ratio, int squareSize);

	@Nonnull
	public String getColor() {
		return color;
	}
	public void setColor( @Nonnull String color) {
		this.color = color;
	}

	public abstract Panel createPanel(String id);
	
	public abstract String getMarkerState();

	public String calculateState() {
		StringBuilder state = new StringBuilder();
		state.append("{x=");
		state.append(getOffsetX());
		state.append(";y=");
		state.append(getOffsetY());
		state.append(";r=");
		state.append(getExtent());
		state.append(";c=");
		state.append(getColor());
		state.append(getMarkerState());
		state.append("}");
		
		
		return state.toString();
	}



}
