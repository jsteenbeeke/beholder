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
package com.jeroensteenbeeke.topiroll.beholder.beans.impl;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.model.UploadResult;
import com.jeroensteenbeeke.lux.ActionResult;
import com.jeroensteenbeeke.hyperion.util.ImageUtil;
import com.jeroensteenbeeke.lux.TypedResult;
import com.jeroensteenbeeke.topiroll.beholder.beans.RemoteImageService;
import com.jeroensteenbeeke.topiroll.beholder.beans.data.ImageType;
import org.springframework.beans.factory.annotation.Value;

import java.io.InputStream;
import java.util.UUID;

public class AmazonS3Service implements RemoteImageService {
	private final String amazonBucketName;

	private final TransferManager transferManager;

	private final AmazonS3 s3;

	public AmazonS3Service(TransferManager transferManager,
							   AmazonS3 s3,
							   @Value("${amazon.bucketname}") String amazonBucketName) {
		this.transferManager = transferManager;
		this.s3 = s3;
		this.amazonBucketName = amazonBucketName;
	}

	@Override
	public TypedResult<String> uploadImage(ImageType imageType, String mimeType, InputStream
			image, long imageSize) {
		final UUID uuid = UUID.randomUUID();
		final String extension = ImageUtil.getExtensionByMimeType(mimeType);

		final String fileName = String.format("%s/%s%s", imageType.name().toLowerCase(), uuid
				.toString(), extension);

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(imageSize);
		metadata.setContentType(mimeType);

		Upload upload = transferManager
				.upload(amazonBucketName, fileName, image, metadata);
		try {
			UploadResult uploadResult = upload.waitForUploadResult();

			return TypedResult.ok(uploadResult.getKey());
		} catch (InterruptedException e) {
			return TypedResult.fail("Upload failed: %s", e.getMessage());
		}
	}

	@Override
	public ActionResult removeImage(String imageKey) {

		try {
			s3.deleteObject(new DeleteObjectRequest(amazonBucketName, imageKey));

			return ActionResult.ok();
		} catch (SdkClientException e) {
			return ActionResult.error(e.getMessage());
		}
	}
}
