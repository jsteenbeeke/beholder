package com.jeroensteenbeeke.topiroll.beholder.jobs;

import com.jeroensteenbeeke.hyperion.tardis.scheduler.HyperionTask;
import com.jeroensteenbeeke.hyperion.tardis.scheduler.ServiceProvider;
import com.jeroensteenbeeke.hyperion.util.ImageUtil;
import com.jeroensteenbeeke.lux.TypedResult;
import com.jeroensteenbeeke.topiroll.beholder.Jobs;
import com.jeroensteenbeeke.topiroll.beholder.beans.RemoteImageService;
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

public class MigrateImagesToAmazonJob extends HyperionTask {
	private static final Logger log = LoggerFactory.getLogger(MigrateImagesToAmazonJob.class);

	public  MigrateImagesToAmazonJob() {
		super("Migrates images stored in the database to Amazon S3", Jobs.Initialize);
	}



	@Override
	public void run(ServiceProvider provider) {
		RemoteImageService remoteImageService = provider.getService(RemoteImageService.class);
		PortraitDAO portraitDAO = provider.getService(PortraitDAO.class);
		ScaledMapDAO mapDAO = provider.getService(ScaledMapDAO.class);
		TokenDefinitionDAO tokenDefinitionDAO = provider.getService(TokenDefinitionDAO.class);

		migrateMaps(mapDAO, remoteImageService);
		migrateTokens(tokenDefinitionDAO, remoteImageService);
		migratePortraits(portraitDAO, remoteImageService);
	}

	private void migratePortraits(PortraitDAO portraitDAO, RemoteImageService s3) {
		PortraitFilter filter = new PortraitFilter();
		filter.amazonKey().isNull();

		for (Portrait portrait: portraitDAO.findByFilter(filter)) {
			TypedResult<String> uploadResult = uploadBlobAs(s3, RemoteImageService.ImageType
					.PORTRAIT, portrait.getData());
			uploadResult.map(key -> {
				portrait.setAmazonKey(key);
				portraitDAO.update(portrait);

				return portrait;
			}).ifNotOk(log::error);
		}
	}


	private void migrateTokens(TokenDefinitionDAO tokenDefinitionDAO, RemoteImageService s3) {
		TokenDefinitionFilter filter = new TokenDefinitionFilter();
		filter.amazonKey().isNull();

		for (TokenDefinition def: tokenDefinitionDAO.findByFilter(filter)) {
			TypedResult<String> uploadResult = uploadBlobAs(s3, RemoteImageService.ImageType
					.TOKEN, def.getImageData());
			uploadResult.map(key -> {
				def.setAmazonKey(key);
				tokenDefinitionDAO.update(def);

				return def;
			}).ifNotOk(log::error);
		}
	}

	private void migrateMaps(ScaledMapDAO mapDAO, RemoteImageService s3) {
		ScaledMapFilter filter = new ScaledMapFilter();
		filter.amazonKey().isNull();

		for (ScaledMap map: mapDAO.findByFilter(filter)) {
			TypedResult<String> uploadResult =
					uploadBlobAs(s3, RemoteImageService.ImageType.MAP, map.getData());
			uploadResult.map((String key) -> {
				map.setAmazonKey(key);
				mapDAO.update(map);

				return map;
			}).ifNotOk(log::error);
		}
	}

	private TypedResult<String> uploadBlobAs(RemoteImageService s3, RemoteImageService.ImageType
			imageType, Blob data) {
		if (data == null) {
			return TypedResult.fail("Image has no data");
		}


		try {
			String mimeType = ImageUtil.getMimeType(data.getBytes(0, 9));

			InputStream stream = data.getBinaryStream();

			return s3.uploadImage(imageType, mimeType, stream, data.length());
		} catch (SQLException e) {
			return TypedResult.fail(e.getMessage());
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
