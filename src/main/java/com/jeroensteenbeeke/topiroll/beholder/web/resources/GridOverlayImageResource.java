/**
 * This file is part of Beholder
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

public class GridOverlayImageResource extends DynamicImageResource {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(ImageUtil.class);

	private final byte[] baseImage;

	private final SerializableSupplier<Integer> squareSize;

	private final SerializableSupplier<Integer> offsetX;

	private final SerializableSupplier<Integer> offsetY;

	public GridOverlayImageResource(byte[] baseImage,
			SerializableSupplier<Integer> squareSize,
			SerializableSupplier<Integer> offsetX,
			SerializableSupplier<Integer> offsetY) {
		this.baseImage = baseImage;
		this.squareSize = squareSize;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}

	@Override
	protected byte[] getImageData(Attributes attributes) {

		return overlay(baseImage, squareSize.get(), offsetX.get(),
				offsetY.get());
	}

	public static byte[] overlay(byte[] image, int squareSize, int offsetX,
			int offsetY) {
		InputStream imageStream = new ByteArrayInputStream(image);

		BufferedImage sourceImage;

		try {
			sourceImage = (BufferedImage) ImageIO.read(imageStream);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			return image;
		}

		Graphics2D graphics2D = sourceImage.createGraphics();

		int left = offsetX % squareSize;
		int top = offsetY % squareSize;

		graphics2D.setColor(Color.RED);

		for (int x = left; x < sourceImage.getWidth(); x = x + squareSize) {
			graphics2D.drawLine(x, 0, x, sourceImage.getHeight());
		}

		for (int y = top; y < sourceImage.getHeight(); y = y + squareSize) {
			graphics2D.drawLine(0, y, sourceImage.getWidth(), y);
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {
			ImageIO.write(sourceImage, ImageUtil.getBlobType(image), out);

			out.flush();

			byte[] newImage = out.toByteArray();

			return newImage;

		} catch (IOException e) {
			log.error(e.getMessage(), e);
			return image;
		}

	}
}
