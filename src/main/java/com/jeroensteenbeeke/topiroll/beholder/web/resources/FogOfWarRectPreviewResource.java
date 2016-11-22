package com.jeroensteenbeeke.topiroll.beholder.web.resources;

import java.awt.Color;
import java.awt.Graphics2D;
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

public class FogOfWarRectPreviewResource extends DynamicImageResource {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(ImageUtil.class);

	private final byte[] baseImage;

	private final SerializableSupplier<Integer> widthSupplier;
	
	private final SerializableSupplier<Integer> heightSupplier;

	private final SerializableSupplier<Integer> offsetX;

	private final SerializableSupplier<Integer> offsetY;

	

	public FogOfWarRectPreviewResource(byte[] baseImage,
			SerializableSupplier<Integer> widthSupplier,
			SerializableSupplier<Integer> heightSupplier,
			SerializableSupplier<Integer> offsetX,
			SerializableSupplier<Integer> offsetY) {
		this.baseImage = baseImage;
		this.widthSupplier = widthSupplier;
		this.heightSupplier = heightSupplier;
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

		int left = offsetX.get();
		int top = offsetY.get();
		int width = widthSupplier.get();
		int height = heightSupplier.get();

		graphics2D.setColor(Color.RED);
		graphics2D.drawRect(left, top, width, height);

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
