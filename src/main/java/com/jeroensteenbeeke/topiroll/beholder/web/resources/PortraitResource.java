package com.jeroensteenbeeke.topiroll.beholder.web.resources;

import com.jeroensteenbeeke.topiroll.beholder.BeholderApplication;
import com.jeroensteenbeeke.topiroll.beholder.dao.PortraitDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.Portrait;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.DynamicImageResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.caching.IResourceCachingStrategy;
import org.apache.wicket.request.resource.caching.NoOpResourceCachingStrategy;
import org.apache.wicket.util.string.StringValue;

import java.sql.Blob;

public class PortraitResource  extends BlobResource {

	private static final long serialVersionUID = 1L;

	private Long fixedPortraitId;

	public PortraitResource() {
	}



	public PortraitResource(Long fixedPortraitId) {
		this.fixedPortraitId = fixedPortraitId;
	}

	@Override
	protected ResourceResponse newResourceResponse(Attributes attributes) {
		PageParameters parameters = attributes.getParameters();

		StringValue tokenId = parameters.get("portraitId");

		if (!tokenId.isEmpty()
				&& !tokenId.isNull()) {

			long token_id = tokenId.toLong();

			PortraitDAO portraitDAO = BeholderApplication.get()
					.getApplicationContext().getBean(PortraitDAO.class);
			Portrait portrait = portraitDAO.load(token_id);

			if (portrait != null) {
				return convertBlobToResponse(attributes, portrait.getId(), portrait::getData);
			}

		} else if (fixedPortraitId != null) {
			PortraitDAO portraitDAO = BeholderApplication.get()
					.getApplicationContext().getBean(PortraitDAO.class);
			Portrait portrait = portraitDAO.load(fixedPortraitId);

			if (portrait != null) {
				return convertBlobToResponse(attributes, portrait.getId(), portrait::getData);
			}
		}

		// Smallest GIF possible
		return null;
	}
}
