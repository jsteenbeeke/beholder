/**
 * This file is part of Beholder
 * (C) 2016-2019 Jeroen Steenbeeke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * This file is part of Beholder
 * (C) 2016 Jeroen Steenbeeke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jeroensteenbeeke.topiroll.beholder.beans;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jeroensteenbeeke.topiroll.beholder.beans.URLService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;

@Component
public class URLService {
	private static final String SOURCE_URL_CONSTANT = "${application.sourceurl}";

	@Value("${application.baseurl}")
	private String urlPrefix;
	
	@Value(SOURCE_URL_CONSTANT)
	private String sourceUrl;

	@Nonnull
	public String contextRelative(@Nonnull String relativePath) {
		String prefix = urlPrefix;

		while (prefix.endsWith("/")) {
			prefix = prefix.substring(0, prefix.length() - 1);
		}

		String path = relativePath;
		while (path.startsWith("/")) {
			path = path.substring(1);
		}

		return String.format("%s/%s", prefix, path);
	}
	
	@Nonnull
	public String getSourceURL() {
		if (sourceUrl.equals(SOURCE_URL_CONSTANT)) {
			return "";
		}
		
		return sourceUrl;
	}
}
