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
package com.jeroensteenbeeke.topiroll.beholder.web.pages.tabletop;

import com.google.common.collect.ImmutableList;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.BootstrapBasePage;
import com.jeroensteenbeeke.topiroll.beholder.BeholderRegistry;
import com.jeroensteenbeeke.topiroll.beholder.dao.MapViewDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.MapViewFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.components.MapCanvas;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.HomePage;
import io.vavr.control.Option;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.protocol.ws.api.WebSocketBehavior;
import org.apache.wicket.protocol.ws.api.WicketWebSocketJQueryResourceReference;
import org.apache.wicket.protocol.ws.api.message.ClosedMessage;
import org.apache.wicket.protocol.ws.api.message.ConnectedMessage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.util.string.StringValue;

import javax.inject.Inject;
import java.util.List;

public class MusicPage extends BootstrapBasePage {

	private static final long serialVersionUID = -6582805616160586051L;
	@Inject
	private MapViewDAO viewDAO;

	private long viewId;

	public MusicPage(PageParameters params) {
		super("Beholder FM");

		StringValue identifier = params.get("identifier");
		if (identifier.isNull() || identifier.isEmpty()) {
			throw new RestartResponseAtInterceptPageException(HomePage.class);
		}

		MapViewFilter filter = new MapViewFilter();
		filter.identifier().set(identifier.toOptionalString());

		Option<MapView> currentView = viewDAO.getUniqueByFilter(filter);
		if (currentView.isEmpty()) {
			throw new RestartResponseAtInterceptPageException(HomePage.class);
		}

		viewId = currentView.map(MapView::getId).get();

		add(new ContextImage("image", "img/logo.png"));
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		add(new WebSocketBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onClose(ClosedMessage message) {
				super.onClose(message);

				BeholderRegistry.instance.removeSession(message.getSessionId(),
						message.getKey());
			}

			@Override
			protected void onConnect(ConnectedMessage message) {
				super.onConnect(message);

				BeholderRegistry.instance
						.addLiveSession(message.getSessionId())
						.withKey(message.getKey())
						.withMarkupId("player").forView(viewId);
			}

		});
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);

		HeaderItem wicketWebsocket = JavaScriptHeaderItem.forReference(WicketWebSocketJQueryResourceReference.get());

		response.render(wicketWebsocket);

		response.render(JavaScriptHeaderItem
				.forReference(new JavaScriptResourceReference(MapCanvas.class,
						"js/musicplayer.js") {
					private static final long serialVersionUID = 9156599767555964681L;

					@Override
					public List<HeaderItem> getDependencies() {
						return ImmutableList.of(wicketWebsocket);
					}
				}));
	}
}
