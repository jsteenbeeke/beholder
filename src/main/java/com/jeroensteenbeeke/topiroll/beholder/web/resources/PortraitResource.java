package com.jeroensteenbeeke.topiroll.beholder.web.resources;

import com.jeroensteenbeeke.topiroll.beholder.BeholderApplication;
import com.jeroensteenbeeke.topiroll.beholder.dao.PortraitDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.Portrait;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.DynamicImageResource;
import org.apache.wicket.request.resource.caching.IResourceCachingStrategy;
import org.apache.wicket.request.resource.caching.NoOpResourceCachingStrategy;
import org.apache.wicket.util.string.StringValue;

public class PortraitResource  extends DynamicImageResource {

	private static final long serialVersionUID = 1L;

	private Long fixedPortraitId;

	public PortraitResource() {
	}



	public PortraitResource(Long fixedPortraitId) {
		this.fixedPortraitId = fixedPortraitId;
	}



	@Override
	protected byte[] getImageData(Attributes attributes) {
		PageParameters parameters = attributes.getParameters();

		StringValue tokenId = parameters.get("portraitId");

		if (!tokenId.isEmpty()
				&& !tokenId.isNull()) {

			long token_id = tokenId.toLong();

			PortraitDAO portraitDAO = BeholderApplication.get()
					.getApplicationContext().getBean(PortraitDAO.class);
			Portrait portrait = portraitDAO.load(token_id);

			if (portrait != null) {

				return portrait.getData();

			}

		} else if (fixedPortraitId != null) {
			PortraitDAO portraitDAO = BeholderApplication.get()
					.getApplicationContext().getBean(PortraitDAO.class);
			Portrait portrait = portraitDAO.load(fixedPortraitId);

			if (portrait != null) {

				return portrait.getData();

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
