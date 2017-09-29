/**
 * This file is part of Beholder
 * (C) 2016 Jeroen Steenbeeke
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jeroensteenbeeke.topiroll.beholder.web.resources;

import org.apache.wicket.markup.html.image.resource.BlobImageResource;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.DynamicImageResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceStreamResource;
import org.apache.wicket.request.resource.caching.IResourceCachingStrategy;
import org.apache.wicket.request.resource.caching.NoOpResourceCachingStrategy;
import org.apache.wicket.util.string.StringValue;

import com.jeroensteenbeeke.topiroll.beholder.BeholderApplication;
import com.jeroensteenbeeke.topiroll.beholder.dao.ScaledMapDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;

public class MapResource extends BlobImageResource {

	private static final long serialVersionUID = 1L;


	@Override
	protected Blob getBlob(Attributes attributes) {
		PageParameters parameters = attributes.getParameters();

		StringValue mapId = parameters.get("mapId");

		if (!mapId.isNull() && !mapId.isEmpty()) {

			long id = mapId.toLong();

			ScaledMapDAO mapDAO = BeholderApplication.get()
					.getApplicationContext().getBean(ScaledMapDAO.class);
			ScaledMap map = mapDAO.load(id);

			if (map != null) {
				return map.getData();
			}

		}

		setFormat("gif");
		return null;

	}

	@Override
	protected IResourceCachingStrategy getCachingStrategy() {
		return NoOpResourceCachingStrategy.INSTANCE;
	}
}
