package com.jeroensteenbeeke.topiroll.beholder.web.resources;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import org.apache.wicket.model.IModel;
import org.danekja.java.util.function.serializable.SerializableSupplier;

import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;

public class FogOfWarCirclePreviewResource
		extends AbstractFogOfWarPreviewResource {

	private static final long serialVersionUID = 1L;

	private final SerializableSupplier<Integer> radiusSupplier;

	private final SerializableSupplier<Integer> offsetX;

	private final SerializableSupplier<Integer> offsetY;

	public FogOfWarCirclePreviewResource(IModel<ScaledMap> mapModel,
			SerializableSupplier<Integer> radiusSupplier,
			SerializableSupplier<Integer> offsetX,
			SerializableSupplier<Integer> offsetY) {
		super(mapModel);
		this.radiusSupplier = radiusSupplier;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}

	@Override
	public void drawShape(Graphics2D graphics2D) {
		int centerX = offsetX.get();
		int centerY = offsetY.get();
		int radius = radiusSupplier.get();

		Shape circle = new Ellipse2D.Double(centerX - radius, centerY - radius,
				2.0 * radius, 2.0 * radius);

		graphics2D.setColor(new Color(1.0f, 0.0f, 0.0f, 0.5f));
		graphics2D.fill(circle);
	}

}
