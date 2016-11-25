package com.jeroensteenbeeke.topiroll.beholder.web.resources;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.DynamicImageResource;
import org.apache.wicket.request.resource.caching.IResourceCachingStrategy;
import org.apache.wicket.request.resource.caching.QueryStringWithVersionResourceCachingStrategy;
import org.apache.wicket.request.resource.caching.version.MessageDigestResourceVersion;
import org.apache.wicket.util.time.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeroensteenbeeke.hyperion.util.ImageUtil;
import com.jeroensteenbeeke.topiroll.beholder.BeholderApplication;
import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarShapeDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.FogOfWarShapeFilter;

public abstract class AbstractFogOfWarPreviewResource
		extends DynamicImageResource {
	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(ImageUtil.class);

	private static final IResourceCachingStrategy strategy = new QueryStringWithVersionResourceCachingStrategy(new MessageDigestResourceVersion());

	private final IModel<ScaledMap> mapModel;

	protected AbstractFogOfWarPreviewResource(IModel<ScaledMap> mapModel) {
		this.mapModel = mapModel;
		
	}

	@Override
	protected final byte[] getImageData(Attributes attributes) {
		ScaledMap map = mapModel.getObject();
		InputStream imageStream = new ByteArrayInputStream(map.getData());

		BufferedImage sourceImage;

		setLastModifiedTime(Time.now());
		
		try {
			sourceImage = (BufferedImage) ImageIO.read(imageStream);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			return map.getData();
		}

		Graphics2D graphics2D = sourceImage.createGraphics();

		drawShape(graphics2D);

		if (shouldDrawExistingShapes()) {
			FogOfWarShapeDAO shapeDAO = BeholderApplication.get()
					.getBean(FogOfWarShapeDAO.class);
			FogOfWarShapeFilter filter = new FogOfWarShapeFilter();
			filter.map().set(map);

			shapeDAO.findByFilter(filter).forEach(s -> {
				s.drawPreviewTo(graphics2D);
			});
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {
			ImageIO.write(sourceImage, ImageUtil.getBlobType(map.getData()),
					out);

			out.flush();

			byte[] newImage = out.toByteArray();

			return postProcess(newImage);

		} catch (IOException e) {
			log.error(e.getMessage(), e);
			return map.getData();
		}

	}
		
	protected boolean shouldDrawExistingShapes() {
		return true;
	}
	
	protected byte[] postProcess(byte[] image) {
		return image;
	}

	public abstract void drawShape(Graphics2D graphics2D);
	
	@Override
	protected IResourceCachingStrategy getCachingStrategy() {
	
		return strategy;
	}
}
