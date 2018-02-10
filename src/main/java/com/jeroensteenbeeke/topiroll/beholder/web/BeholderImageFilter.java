package com.jeroensteenbeeke.topiroll.beholder.web;

import com.jeroensteenbeeke.hyperion.util.ImageUtil;
import com.jeroensteenbeeke.topiroll.beholder.BeholderApplication;
import com.jeroensteenbeeke.topiroll.beholder.dao.PortraitDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.ScaledMapDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.TokenDefinitionDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.Portrait;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenDefinition;
import org.apache.commons.io.IOUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.EOFException;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BeholderImageFilter implements Filter {
	private static final Pattern MAP_PATTERN = Pattern.compile("^/images/map/([0-9]+)$");
	private static final Pattern PORTRAIT_PATTERN = Pattern.compile("^/images/portrait/([0-9]+)$");
	private static final Pattern TOKEN_PATTERN = Pattern.compile("^/images/token/([0-9]+)$");

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;

		if ("GET".equals(httpServletRequest.getMethod())) {
			String path = httpServletRequest.getServletPath();

			if (path == null || path.isEmpty()) {
				chain.doFilter(request, response);
			} else {
				Matcher mapMatcher = MAP_PATTERN.matcher(path);
				Matcher portraitMatcher = PORTRAIT_PATTERN.matcher(path);
				Matcher tokenMatcher = TOKEN_PATTERN.matcher(path);

				if (mapMatcher.matches()) {
					getMap(httpServletResponse, Long.parseLong(mapMatcher.group(1)));
				} else if (portraitMatcher.matches()) {
					getPortrait(httpServletResponse, Long.parseLong(portraitMatcher.group(1)));
				} else if (tokenMatcher.matches()) {
					getToken(httpServletResponse, Long.parseLong(tokenMatcher.group(1)));
				} else {
					chain.doFilter(request, response);
				}
			}
		} else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() {

	}

	private void getMap(HttpServletResponse response,
					   Long mapId) throws IOException {
		ScaledMap map = BeholderApplication.get().getBean(ScaledMapDAO.class).load(mapId);

		sendBlobToClient(response, map, ScaledMap::getData);
	}

	private void getPortrait(HttpServletResponse response,
							 Long portraitId) throws IOException {
		Portrait portrait = BeholderApplication.get().getBean(PortraitDAO.class).load(portraitId);

		sendBlobToClient(response, portrait, Portrait::getData);
	}

	private void getToken(HttpServletResponse response,
						  Long tokenId) throws IOException {
		TokenDefinition token =
				BeholderApplication.get().getBean(TokenDefinitionDAO.class).load(tokenId);

		sendBlobToClient(response, token, TokenDefinition::getImageData);
	}

	private <T> void sendBlobToClient(HttpServletResponse response, T input, Function<T, Blob>
			blobExtractor) throws IOException {
		if (input != null) {
			try {
				response.addHeader("Cache-control", "max-age=31536000");
				Blob blob = blobExtractor.apply(input);
				response.addHeader("Content-type", ImageUtil.getMimeType(blob.getBytes(1, 8)));
				IOUtils.copy(blob.getBinaryStream(), response.getOutputStream
						());
			} catch (SQLException e) {
				throw new IOException(e);
			} catch (EOFException e) {
				// Silent ignore, this usually means the client closed the connection
			}
		} else {
			response.sendError(404, "Invalid map ID");
		}
	}
}
