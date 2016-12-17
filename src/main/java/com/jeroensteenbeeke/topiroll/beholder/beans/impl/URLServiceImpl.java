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
package com.jeroensteenbeeke.topiroll.beholder.beans.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jeroensteenbeeke.topiroll.beholder.beans.URLService;

@Component
class URLServiceImpl implements URLService {
	@Value("${application.baseurl}")
	private String urlPrefix;

	@Override
	public String contextRelative(String relativePath) {
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
}
