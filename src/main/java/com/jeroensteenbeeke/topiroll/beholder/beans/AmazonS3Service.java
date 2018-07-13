package com.jeroensteenbeeke.topiroll.beholder.beans;

import com.jeroensteenbeeke.hyperion.util.ActionResult;
import com.jeroensteenbeeke.hyperion.util.ImageUtil;
import com.jeroensteenbeeke.hyperion.util.TypedActionResult;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public interface AmazonS3Service {
	default TypedActionResult<String> uploadImage(ImageType imageType, byte[] image) {
		return uploadImage(imageType, ImageUtil.getMimeType(image), new ByteArrayInputStream
				(image), image.length);
	}

	TypedActionResult<String> uploadImage(ImageType imageType, String mimeType, InputStream
			stream, long imageSize);

	ActionResult removeImage(String imageKey);

	enum ImageType {
		MAP, TOKEN, PORTRAIT
	}
}
