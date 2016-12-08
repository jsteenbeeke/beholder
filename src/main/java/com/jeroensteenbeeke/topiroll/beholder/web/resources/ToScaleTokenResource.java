package com.jeroensteenbeeke.topiroll.beholder.web.resources;

import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.DynamicImageResource;
import org.apache.wicket.request.resource.caching.IResourceCachingStrategy;
import org.apache.wicket.request.resource.caching.NoOpResourceCachingStrategy;
import org.apache.wicket.util.string.StringValue;

import com.jeroensteenbeeke.hyperion.util.ImageUtil;
import com.jeroensteenbeeke.topiroll.beholder.BeholderApplication;
import com.jeroensteenbeeke.topiroll.beholder.dao.MapViewDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.TokenDefinitionDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenDefinition;

public class ToScaleTokenResource extends DynamicImageResource {

	private static final long serialVersionUID = 1L;

	@Override
	protected byte[] getImageData(Attributes attributes) {
		PageParameters parameters = attributes.getParameters();
		IRequestParameters qp = attributes.getRequest().getQueryParameters();

		StringValue viewId = parameters.get("viewId");
		StringValue tokenId = parameters.get("tokenId");
		StringValue preview = qp.getParameterValue("preview");

		if (!viewId.isNull() && !viewId.isEmpty() && !tokenId.isEmpty()
				&& !tokenId.isNull()) {

			long view_id = viewId.toLong();
			long token_id = tokenId.toLong();

			MapViewDAO viewDAO = BeholderApplication.get()
					.getApplicationContext().getBean(MapViewDAO.class);
			MapView view = viewDAO.load(view_id);

			TokenDefinitionDAO definitionDAO = BeholderApplication.get()
					.getApplicationContext().getBean(TokenDefinitionDAO.class);
			TokenDefinition definition = definitionDAO.load(token_id);

			if (view != null && definition != null) {

				byte[] data = definition.getImageData();

				boolean isPreview = false;

				if (!preview.isNull() && !preview.isEmpty()) {
					isPreview = preview.toBoolean(false);
				}

				byte[] resizedData = definition.getSize().resizeFor(data, view,
						isPreview);

				setFormat(ImageUtil.getWicketFormatType(resizedData));

				return resizedData;

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