package com.jeroensteenbeeke.topiroll.beholder.web.resources;

import java.awt.Dimension;

import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.DynamicImageResource;

import com.jeroensteenbeeke.hyperion.util.ImageUtil;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.util.Calculations;
import com.jeroensteenbeeke.topiroll.beholder.util.SimpleResolution;

public class ToScaleMapResource extends DynamicImageResource {

	private static final long serialVersionUID = 1L;

	private final IModel<MapView> viewModel;

	public ToScaleMapResource(final IModel<MapView> viewModel) {
		this.viewModel = viewModel;
	}

	@Override
	protected byte[] getImageData(Attributes attributes) {
		MapView view = viewModel.getObject();

		ScaledMap map = view.getSelectedMap();

		if (map != null) {
			byte[] data = map.getData();

			Dimension dimensions = ImageUtil.getImageDimensions(data);

			setFormat(ImageUtil.getWicketFormatType(data));

			double factor = Calculations.scale(map.getSquareSize())
					.toResolution(new SimpleResolution(view.getWidth(),
							view.getHeight()))
					.onScreenWithDiagonalSize(view.getScreenDiagonalInInches());
			return ImageUtil.resize(data,
					(int) (dimensions.getWidth() * factor),
					(int) (dimensions.getHeight() * factor));
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
