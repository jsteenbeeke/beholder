package com.jeroensteenbeeke.topiroll.beholder.web.resources;

import com.jeroensteenbeeke.hyperion.util.ImageUtil;
import org.apache.wicket.Application;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.IResourceStreamWriter;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.danekja.java.util.function.serializable.SerializableSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;

public abstract class BlobResource extends AbstractResource {

	private static final Logger logger = LoggerFactory.getLogger(MapResource.class);


	protected ResourceResponse convertBlobToResponse(Attributes attributes, Long id, SerializableSupplier<Blob> blobSupplier) {
		final IResourceStream resourceStream = new BlobResourceStream(blobSupplier);
		ResourceResponse data = new ResourceResponse();

		// performance check; don't bother to do anything if the resource is still cached by client
		if (data.dataNeedsToBeWritten(attributes))
		{
			InputStream inputStream = null;
			if (!(resourceStream instanceof IResourceStreamWriter))
			{
				try
				{
					inputStream = resourceStream.getInputStream();
				}
				catch (ResourceStreamNotFoundException e)
				{
					data.setError(HttpServletResponse.SC_NOT_FOUND);
					close(resourceStream);
				}
			}

			data.setContentDisposition(ContentDisposition.INLINE);
			Bytes length = resourceStream.length();
			if (length != null)
			{
				data.setContentLength(length.bytes());
			}
			data.setFileName(Long.toString(id));

			String contentType = resourceStream.getContentType();
			if (contentType == null && Application.exists())
			{
				try {
					contentType = ImageUtil.getMimeType(blobSupplier.get().getBytes(0, 8));
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
					close(resourceStream);
				}
			}
			data.setContentType(contentType);

			if (resourceStream instanceof IResourceStreamWriter)
			{
				data.setWriteCallback(new WriteCallback()
				{
					@Override
					public void writeData(Attributes attributes) throws IOException
					{
						((IResourceStreamWriter)resourceStream).write(attributes.getResponse().getOutputStream());
						close(resourceStream);
					}
				});
			}
			else
			{
				final InputStream s = inputStream;
				data.setWriteCallback(new WriteCallback()
				{
					@Override
					public void writeData(Attributes attributes) throws IOException
					{
						try
						{
							writeStream(attributes, s);
						}
						finally
						{
							close(resourceStream);
						}
					}
				});
			}
		}

		return data;
	}

	private void close(IResourceStream stream)
	{
		try
		{
			stream.close();
		}
		catch (IOException e)
		{
			logger.error("Couldn't close ResourceStream", e);
		}
	}

}
