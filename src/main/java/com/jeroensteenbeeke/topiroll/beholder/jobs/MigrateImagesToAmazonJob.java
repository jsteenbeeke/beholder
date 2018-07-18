package com.jeroensteenbeeke.topiroll.beholder.jobs;

import com.jeroensteenbeeke.hyperion.tardis.scheduler.HyperionTask;
import com.jeroensteenbeeke.hyperion.tardis.scheduler.ServiceProvider;
import com.jeroensteenbeeke.hyperion.util.ImageUtil;
import com.jeroensteenbeeke.hyperion.util.TypedActionResult;
import com.jeroensteenbeeke.topiroll.beholder.Jobs;
import com.jeroensteenbeeke.topiroll.beholder.beans.AmazonS3Service;
import com.jeroensteenbeeke.topiroll.beholder.dao.PortraitDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.ScaledMapDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.TokenDefinitionDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.Portrait;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenDefinition;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.PortraitFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.ScaledMapFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.TokenDefinitionFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.function.Consumer;

public class MigrateImagesToAmazonJob extends HyperionTask {
	private static final Logger log = LoggerFactory.getLogger(MigrateImagesToAmazonJob.class);

	public  MigrateImagesToAmazonJob() {
		super("Migrates images stored in the database to Amazon S3", Jobs.Initialize);
	}



	@Override
	public void run(ServiceProvider provider) {
		AmazonS3Service amazonS3Service = provider.getService(AmazonS3Service.class);
		PortraitDAO portraitDAO = provider.getService(PortraitDAO.class);
		ScaledMapDAO mapDAO = provider.getService(ScaledMapDAO.class);
		TokenDefinitionDAO tokenDefinitionDAO = provider.getService(TokenDefinitionDAO.class);

		migrateMaps(mapDAO, amazonS3Service);
		migrateTokens(tokenDefinitionDAO, amazonS3Service);
		migratePortraits(portraitDAO, amazonS3Service);
	}

	private void migratePortraits(PortraitDAO portraitDAO, AmazonS3Service s3) {
		PortraitFilter filter = new PortraitFilter();
		filter.amazonKey().isNull();

		for (Portrait portrait: portraitDAO.findByFilter(filter)) {
			TypedActionResult<String> uploadResult = uploadBlobAs(s3, AmazonS3Service.ImageType
					.PORTRAIT, portrait.getData());
			uploadResult.ifOk(key -> {
				portrait.setAmazonKey(key);
				portraitDAO.update(portrait);
			});
			uploadResult.ifNotOk((Consumer<String>) log::error);
		}
	}


	private void migrateTokens(TokenDefinitionDAO tokenDefinitionDAO, AmazonS3Service s3) {
		TokenDefinitionFilter filter = new TokenDefinitionFilter();
		filter.amazonKey().isNull();

		for (TokenDefinition def: tokenDefinitionDAO.findByFilter(filter)) {
			TypedActionResult<String> uploadResult = uploadBlobAs(s3, AmazonS3Service.ImageType
					.TOKEN, def.getImageData());
			uploadResult.ifOk(key -> {
				def.setAmazonKey(key);
				tokenDefinitionDAO.update(def);
			});
			uploadResult.ifNotOk((Consumer<String>) log::error);
		}
	}

	private void migrateMaps(ScaledMapDAO mapDAO, AmazonS3Service s3) {
		ScaledMapFilter filter = new ScaledMapFilter();
		filter.amazonKey().isNull();

		for (ScaledMap map: mapDAO.findByFilter(filter)) {
			TypedActionResult<String> uploadResult =
					uploadBlobAs(s3, AmazonS3Service.ImageType.MAP, map.getData());
			uploadResult.ifOk((String key) -> {
				map.setAmazonKey(key);
				mapDAO.update(map);
			});
			uploadResult.ifNotOk((Consumer<String>) log::error);
		}
	}

	private TypedActionResult<String> uploadBlobAs(AmazonS3Service s3, AmazonS3Service.ImageType
			imageType, Blob data) {
		if (data == null) {
			return TypedActionResult.fail("Image has no data");
		}

		String mimeType = getMimeType(ImageUtil.getBlobType(data));

		try {
			InputStream stream = data.getBinaryStream();

			return s3.uploadImage(imageType, mimeType, stream, data.length());
		} catch (SQLException e) {
			return TypedActionResult.fail(e.getMessage());
		}
	}

	private String getMimeType(String blobType) {
		switch (blobType) {
			case "jpg":
				return "image/jpeg";
			default:
				return "image/".concat(blobType);
		}


	}
}
