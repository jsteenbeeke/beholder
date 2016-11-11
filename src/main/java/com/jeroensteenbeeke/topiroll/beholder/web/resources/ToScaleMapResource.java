package com.jeroensteenbeeke.topiroll.beholder.web.resources;

import java.awt.Dimension;

import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.DynamicImageResource;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.StringValueConversionException;

import com.jeroensteenbeeke.hyperion.util.ImageUtil;
import com.jeroensteenbeeke.topiroll.beholder.BeholderApplication;
import com.jeroensteenbeeke.topiroll.beholder.dao.ScaledMapDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.util.Calculations;
import com.jeroensteenbeeke.topiroll.beholder.util.SimpleResolution;

public class ToScaleMapResource extends DynamicImageResource {

	private static final long serialVersionUID = 1L;

	@Override
	protected byte[] getImageData(Attributes attributes) {
		PageParameters parameters = attributes.getParameters();
		IRequestParameters queryParams = attributes.getRequest()
				.getQueryParameters();

		StringValue mapId = parameters.get("mapId");
		StringValue widthValue = queryParams.getParameterValue("w");
		StringValue heightValue = queryParams.getParameterValue("h");
		StringValue diagValue = queryParams.getParameterValue("d");

		if (!widthValue.isNull() && !widthValue.isEmpty()
				&& !heightValue.isNull() && !heightValue.isEmpty()
				&& !mapId.isNull() && !mapId.isEmpty() && !diagValue.isEmpty()
				&& !diagValue.isNull()) {
			try {
				long id = mapId.toLong();

				ScaledMapDAO scaledMapDAO = BeholderApplication.get()
						.getApplicationContext().getBean(ScaledMapDAO.class);
				ScaledMap map = scaledMapDAO.load(id);

				if (map != null) {
					byte[] data = map.getData();

					Dimension dimensions = ImageUtil.getImageDimensions(data);

					setFormat(ImageUtil.getWicketFormatType(data));

					double factor = Calculations.scale(map.getSquareSize())
							.toResolution(new SimpleResolution(
									widthValue.toInt(), heightValue.toInt()))
							.onScreenWithDiagonalSize(diagValue.toDouble());
					return ImageUtil.resize(data,
							(int) (dimensions.getWidth() * factor),
							(int) (dimensions.getHeight() * factor));
				}

			} catch (StringValueConversionException e) {
				// Do not log, just fall through to default return
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
}
