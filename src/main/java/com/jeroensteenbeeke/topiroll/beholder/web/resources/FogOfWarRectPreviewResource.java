/**
 * This file is part of Beholder
 * (C) 2016 Jeroen Steenbeeke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jeroensteenbeeke.topiroll.beholder.web.resources;

import java.awt.Color;
import java.awt.Graphics2D;

import org.apache.wicket.model.IModel;
import org.danekja.java.util.function.serializable.SerializableSupplier;

import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;

public class FogOfWarRectPreviewResource
		extends AbstractFogOfWarPreviewResource {

	private static final long serialVersionUID = 1L;

	private final SerializableSupplier<Integer> widthSupplier;

	private final SerializableSupplier<Integer> heightSupplier;

	private final SerializableSupplier<Integer> offsetX;

	private final SerializableSupplier<Integer> offsetY;

	public FogOfWarRectPreviewResource(IModel<ScaledMap> mapModel,
			SerializableSupplier<Integer> widthSupplier,
			SerializableSupplier<Integer> heightSupplier,
			SerializableSupplier<Integer> offsetX,
			SerializableSupplier<Integer> offsetY) {
		super(mapModel);
		this.widthSupplier = widthSupplier;
		this.heightSupplier = heightSupplier;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}

	@Override
	public void drawShape(Graphics2D graphics2D) {
		int left = offsetX.get();
		int top = offsetY.get();
		int width = widthSupplier.get();
		int height = heightSupplier.get();

		graphics2D.setColor(new Color(1.0f, 0.0f, 0.0f, 0.5f));
		graphics2D.fillRect(left, top, width, height);
	}

}
