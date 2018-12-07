package com.jeroensteenbeeke.topiroll.beholder.beans.impl;

import com.amazonaws.AmazonServiceException;
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
import com.jeroensteenbeeke.topiroll.beholder.beans.AmazonS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

@Component
public class AmazonS3ServiceImpl implements AmazonS3Service {
	private final String amazonBucketName;

	private final TransferManager transferManager;

	private final AmazonS3 s3;

	@Autowired
	public AmazonS3ServiceImpl(TransferManager transferManager,
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
