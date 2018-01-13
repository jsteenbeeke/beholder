package com.jeroensteenbeeke.topiroll.beholder.web.resources;

import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.danekja.java.util.function.serializable.SerializableSupplier;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;

public class BlobResourceStream extends AbstractResourceStream {
	private final SerializableSupplier<Blob> blobSupplier;

	private transient InputStream stream;

	public BlobResourceStream(SerializableSupplier<Blob> blobSupplier) {
		this.blobSupplier = blobSupplier;
	}

	@Override
	public InputStream getInputStream() throws ResourceStreamNotFoundException {
		if (stream == null) {
			try {
				stream = blobSupplier.get().getBinaryStream();
			} catch (SQLException e) {
				throw new ResourceStreamNotFoundException(e);
			}
		}

		return stream;

	}

	@Override
	public void close() throws IOException {
		if (stream != null) {
			stream.close();
		}
	}
}
