package com.jeroensteenbeeke.topiroll.beholder.beans;

import com.jeroensteenbeeke.lux.ActionResult;
import com.jeroensteenbeeke.hyperion.util.ImageUtil;
import com.jeroensteenbeeke.lux.TypedResult;

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

	enum ImageType {
		MAP, TOKEN, PORTRAIT
	}
}
