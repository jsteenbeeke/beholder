/*
 * This file is part of Beholder
 * Copyright (C) 2016 - 2023 Jeroen Steenbeeke
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
package com.jeroensteenbeeke.topiroll.beholder.beans;

import com.jeroensteenbeeke.lux.ActionResult;
import com.jeroensteenbeeke.hyperion.util.ImageUtil;
import com.jeroensteenbeeke.lux.TypedResult;
import com.jeroensteenbeeke.topiroll.beholder.beans.data.ImageType;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public interface RemoteImageService {
	default TypedResult<String> uploadImage(ImageType imageType, byte[] image) {
		return uploadImage(imageType, ImageUtil.getMimeType(image), new ByteArrayInputStream
				(image), image.length);
	}

	TypedResult<String> uploadImage(ImageType imageType, String mimeType, InputStream
			stream, long imageSize);

	ActionResult removeImage(String imageKey);

}
