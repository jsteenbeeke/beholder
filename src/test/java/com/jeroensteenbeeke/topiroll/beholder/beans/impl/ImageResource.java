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
package com.jeroensteenbeeke.topiroll.beholder.beans.impl;

import java.io.*;

public class ImageResource {
	public static File importImage(String name) {
		try {
			File tmp = File.createTempFile(name, ".tmp");
			try (FileOutputStream fos = new FileOutputStream(tmp);
				 InputStream in = ImageResource.class.getResourceAsStream(name)) {
				int i;
				while ((i = in.read()) != -1) {
					fos.write(i);
				}
			}

			return tmp;
		} catch (IOException ioe) {
			throw new IllegalStateException("Developer done fucked up");
		}

	}

	public static byte[] getImageAsByteArray(String name) {
		try{
			try  (ByteArrayOutputStream out = new ByteArrayOutputStream();
				 InputStream in = ImageResource.class.getResourceAsStream(name)) {
				int i;
				while ((i = in.read()) != -1) {
					out.write(i);
				}

				return out.toByteArray();
			}
		} catch (IOException ioe) {
			throw new IllegalStateException("Developer done fucked up");
		}

	}
}
