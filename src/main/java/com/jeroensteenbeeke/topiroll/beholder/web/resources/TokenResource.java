/**
 * This file is part of Beholder
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jeroensteenbeeke.topiroll.beholder.web.resources;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.DynamicImageResource;
import org.apache.wicket.request.resource.caching.IResourceCachingStrategy;
import org.apache.wicket.request.resource.caching.NoOpResourceCachingStrategy;
import org.apache.wicket.util.string.StringValue;

import com.jeroensteenbeeke.topiroll.beholder.BeholderApplication;
import com.jeroensteenbeeke.topiroll.beholder.dao.TokenDefinitionDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenDefinition;

public class TokenResource extends DynamicImageResource {

	private static final long serialVersionUID = 1L;
	
	private Long fixedTokenId;
	
	public TokenResource() {
	}
	
	

	public TokenResource(Long fixedTokenId) {
		this.fixedTokenId = fixedTokenId;
	}



	@Override
	protected byte[] getImageData(Attributes attributes) {
		PageParameters parameters = attributes.getParameters();

		StringValue tokenId = parameters.get("tokenId");

		if (!tokenId.isEmpty()
				&& !tokenId.isNull()) {

			long token_id = tokenId.toLong();

			TokenDefinitionDAO definitionDAO = BeholderApplication.get()
					.getApplicationContext().getBean(TokenDefinitionDAO.class);
			TokenDefinition definition = definitionDAO.load(token_id);

			if (definition != null) {

				return definition.getImageData();

			}

		} else if (fixedTokenId != null) {
			TokenDefinitionDAO definitionDAO = BeholderApplication.get()
					.getApplicationContext().getBean(TokenDefinitionDAO.class);
			TokenDefinition definition = definitionDAO.load(fixedTokenId);

			if (definition != null) {

				return definition.getImageData();

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
