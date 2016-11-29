package com.jeroensteenbeeke.topiroll.beholder.web.resources;

import java.awt.Dimension;

import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.DynamicImageResource;
import org.apache.wicket.request.resource.caching.IResourceCachingStrategy;
import org.apache.wicket.request.resource.caching.NoOpResourceCachingStrategy;
import org.apache.wicket.util.string.StringValue;

import com.jeroensteenbeeke.hyperion.util.ImageUtil;
import com.jeroensteenbeeke.topiroll.beholder.BeholderApplication;
import com.jeroensteenbeeke.topiroll.beholder.dao.MapViewDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.util.Calculations;
import com.jeroensteenbeeke.topiroll.beholder.util.SimpleResolution;

public class ToScaleMapResource extends DynamicImageResource {

	private static final long serialVersionUID = 1L;

	@Override
	protected byte[] getImageData(Attributes attributes) {
		PageParameters parameters = attributes.getParameters();
		IRequestParameters qp = attributes.getRequest().getQueryParameters();

		StringValue viewId = parameters.get("viewId");
		StringValue preview = qp.getParameterValue("preview");

		if (!viewId.isNull() && !viewId.isEmpty()) {

			long id = viewId.toLong();

			MapViewDAO viewDAO = BeholderApplication.get()
					.getApplicationContext().getBean(MapViewDAO.class);
			MapView view = viewDAO.load(id);

			if (view != null) {

				ScaledMap map = view.getSelectedMap();

				if (map != null) {
					byte[] data = map.getData();

					Dimension dimensions = ImageUtil.getImageDimensions(data);

					setFormat(ImageUtil.getWicketFormatType(data));

					double factor = Calculations.scale(map.getSquareSize())
							.toResolution(new SimpleResolution(view.getWidth(),
									view.getHeight()))
							.onScreenWithDiagonalSize(
									view.getScreenDiagonalInInches());
					int targetWidth = (int) (dimensions.getWidth() * factor);
					int targetHeight = (int) (dimensions.getHeight() * factor);

					if (!preview.isNull() && !preview.isEmpty()) {
						boolean isPreview = preview.toBoolean(false);

						if (isPreview) {
							targetWidth = (int) dimensions.getWidth();
							targetHeight = (int) dimensions.getHeight();

							// Decrease width and height by 10% until preview
							// size has been achieved
							while (targetWidth > 640) {
								targetWidth = (int) (targetWidth * 0.9);
								targetHeight = (int) (targetHeight * 0.9);
							}
						}
					}

					return ImageUtil.resize(data, targetWidth, targetHeight);
				}
			}

		}

		setFormat("gif");

		// Smallest GIF possible
		return new byte[] { 0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0x01, 0x00,
				0x01, 0x00, 0x00, 0x00, 0x00, 0x21, (byte) 0xF9, 0x04, 0x01,
				0x00, 0x00, 0x00, 0x00, 0x2C, 0x00, 0x00, 0x00, 0x00, 0x01,
				0x00, 0x01, 0x00, 0x00, 0x02

		};
	}

	@Override
	protected IResourceCachingStrategy getCachingStrategy() {
		return NoOpResourceCachingStrategy.INSTANCE;
	}
}
