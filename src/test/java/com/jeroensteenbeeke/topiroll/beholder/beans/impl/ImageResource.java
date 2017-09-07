package com.jeroensteenbeeke.topiroll.beholder.beans.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
}
