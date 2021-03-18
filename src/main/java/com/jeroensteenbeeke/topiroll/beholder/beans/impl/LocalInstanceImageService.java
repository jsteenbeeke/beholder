/**
 * This file is part of Beholder
 * (C) 2016-2019 Jeroen Steenbeeke
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
package com.jeroensteenbeeke.topiroll.beholder.beans.impl;

import com.jeroensteenbeeke.hyperion.util.ImageUtil;
import com.jeroensteenbeeke.lux.ActionResult;
import com.jeroensteenbeeke.lux.TypedResult;
import com.jeroensteenbeeke.topiroll.beholder.beans.RemoteImageService;
import com.jeroensteenbeeke.topiroll.beholder.beans.data.ImageType;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.InputStream;
import java.nio.ByteBuffer;

public class LocalInstanceImageService implements RemoteImageService {
	@Override
	public TypedResult<String> uploadImage(ImageType imageType, byte[] image) {
		return internalUploadImage(imageType, ImageUtil.getMimeType(image), image, image.length);
	}

	private TypedResult<String> internalUploadImage(ImageType imageType, String mimeType, byte[] data, long length) {
		OkHttpClient client = new OkHttpClient();

		Request req = new Request.Builder()
				.url("http://localhost:4040/images/")
				.post(RequestBody.create(MediaType.parse(mimeType), data)).build();

		return TypedResult.ok(req).map(r -> client.newCall(r).execute())
				.map(r -> r.body().string());
	}

	@Override
	public TypedResult<String> uploadImage(ImageType imageType, String mimeType, InputStream stream, long imageSize) {
		ByteBuffer buffer = ByteBuffer.allocate((int) imageSize);

		return TypedResult.ok(buffer).flatMap(buf -> {
			int i;
			while ((i = stream.read()) != -1) {
				buf.put((byte) i);
			}

			return internalUploadImage(imageType, mimeType, buffer.array(), imageSize);
		});
	}

	@Override
	public ActionResult removeImage(String imageKey) {
		// Say we removed the image, but don't actually, since the images go poof on shutdown anyway
		return ActionResult.ok();
	}
}
