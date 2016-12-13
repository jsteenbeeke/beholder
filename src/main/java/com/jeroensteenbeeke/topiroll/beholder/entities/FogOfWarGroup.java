package com.jeroensteenbeeke.topiroll.beholder.entities;

import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.persistence.*;

import org.apache.wicket.model.IModel;

import com.jeroensteenbeeke.hyperion.data.BaseDomainObject;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.hyperion.util.ImageUtil;
import com.jeroensteenbeeke.topiroll.beholder.web.resources.AbstractFogOfWarPreviewResource;

@Entity
public class FogOfWarGroup extends BaseDomainObject
		implements ICanHazVisibilityStatus {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(sequenceName = "SEQ_ID_FogOfWarGroup",
			name = "FogOfWarGroup", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "FogOfWarGroup",
			strategy = GenerationType.SEQUENCE)
	@Access(value = AccessType.PROPERTY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "map")

	private ScaledMap map;

	@OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
	private List<FogOfWarGroupVisibility> visibilities = new ArrayList<FogOfWarGroupVisibility>();

	@Column(nullable = false)
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

	public void setMap(@Nonnull ScaledMap map) {
		this.map = map;
	}

	@Nonnull
	public String getName() {
		return name;
	}

	public void setName(@Nonnull String name) {
		this.name = name;
	}

	@Override
	public String getDescription() {
		return getName();
	}

	@Override
	public AbstractFogOfWarPreviewResource createThumbnailResource(int size) {
		IModel<List<FogOfWarShape>> shapesModel = ModelMaker
				.wrapList(getShapes());

		return new AbstractFogOfWarPreviewResource(ModelMaker.wrap(getMap())) {
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean shouldDrawExistingShapes() {
				return false;
			}
			
			@Override
			protected byte[] postProcess(byte[] image) {
				return ImageUtil.resize(image, size, size);
			}

			@Override
			public void drawShape(Graphics2D graphics2d) {
				shapesModel.detach();
				shapesModel.getObject().forEach(s -> {
					s.createThumbnailResource(size).drawShape(graphics2d);
				});
				shapesModel.detach();
			}
		};
	}

	@Nonnull
	public List<FogOfWarGroupVisibility> getVisibilities() {
		return visibilities;
	}

	public void setVisibilities(
			@Nonnull List<FogOfWarGroupVisibility> visibilities) {
		this.visibilities = visibilities;
	}

	@Override
	public VisibilityStatus getStatus(MapView view) {

		return getVisibilities().stream().filter(v -> v.getView().equals(view))
				.findAny().map(FogOfWarGroupVisibility::getStatus)
				.orElse(VisibilityStatus.INVISIBLE);
	}

}
