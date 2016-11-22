package com.jeroensteenbeeke.topiroll.beholder.web.resources;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.wicket.request.resource.DynamicImageResource;
import org.danekja.java.util.function.serializable.SerializableSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeroensteenbeeke.hyperion.util.ImageUtil;

public class FogOfWarCirclePreviewResource extends DynamicImageResource {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(ImageUtil.class);

	private final byte[] baseImage;

	private final SerializableSupplier<Integer> radiusSupplier;

	private final SerializableSupplier<Integer> offsetX;

	private final SerializableSupplier<Integer> offsetY;

	public FogOfWarCirclePreviewResource(byte[] baseImage,
			SerializableSupplier<Integer> radiusSupplier,
			SerializableSupplier<Integer> offsetX,
			SerializableSupplier<Integer> offsetY) {
		this.baseImage = baseImage;
		this.radiusSupplier = radiusSupplier;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}

	@Override
	protected byte[] getImageData(Attributes attributes) {

		InputStream imageStream = new ByteArrayInputStream(baseImage);

		BufferedImage sourceImage;

		try {
			sourceImage = (BufferedImage) ImageIO.read(imageStream);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			return baseImage;
		}

		Graphics2D graphics2D = sourceImage.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

		int centerX = offsetX.get();
		int centerY = offsetY.get();
		int radius = radiusSupplier.get();

		Shape circle = new Ellipse2D.Double(centerX - radius, centerY - radius,
				2.0 * radius, 2.0 * radius);

		graphics2D.setColor(Color.RED);
		graphics2D.draw(circle);

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {
			ImageIO.write(sourceImage, ImageUtil.getBlobType(baseImage), out);

			out.flush();

			byte[] newImage = out.toByteArray();

			return newImage;

		} catch (IOException e) {
			log.error(e.getMessage(), e);
			return baseImage;
		}

	}
}
