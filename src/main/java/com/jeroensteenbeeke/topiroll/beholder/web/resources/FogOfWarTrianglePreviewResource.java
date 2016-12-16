package com.jeroensteenbeeke.topiroll.beholder.web.resources;

import java.awt.Color;
import java.awt.Graphics2D;

import org.apache.wicket.model.IModel;
import org.danekja.java.util.function.serializable.SerializableSupplier;

import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.TriangleOrientation;

public class FogOfWarTrianglePreviewResource
		extends AbstractFogOfWarPreviewResource {

	private static final long serialVersionUID = 1L;

	private final SerializableSupplier<Integer> widthSupplier;

	private final SerializableSupplier<Integer> heightSupplier;

	private final SerializableSupplier<Integer> offsetX;

	private final SerializableSupplier<Integer> offsetY;
	
	private final SerializableSupplier<TriangleOrientation> orientation;

	public FogOfWarTrianglePreviewResource(IModel<ScaledMap> mapModel,
			SerializableSupplier<Integer> widthSupplier,
			SerializableSupplier<Integer> heightSupplier,
			SerializableSupplier<Integer> offsetX,
			SerializableSupplier<Integer> offsetY,
			SerializableSupplier<TriangleOrientation> orientation) {
		super(mapModel);
		this.widthSupplier = widthSupplier;
		this.heightSupplier = heightSupplier;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.orientation = orientation;
	}

	@Override
	public void drawShape(Graphics2D graphics2D) {
		int left = offsetX.get();
		int top = offsetY.get();
		int width = widthSupplier.get();
		int height = heightSupplier.get();
		TriangleOrientation or = orientation.get();

		graphics2D.setColor(new Color(1.0f, 0.0f, 0.0f, 0.5f));
		graphics2D.fill(or.toPolygon(left, top, width, height));
		
	}

}
