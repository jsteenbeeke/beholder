package com.jeroensteenbeeke.topiroll.beholder.web.pages.tabletop;

import com.google.common.collect.ImmutableList;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.BootstrapBasePage;
import com.jeroensteenbeeke.topiroll.beholder.BeholderRegistry;
import com.jeroensteenbeeke.topiroll.beholder.dao.MapViewDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.MapViewFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.components.MapCanvas;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.HomePage;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.ajax.WicketEventJQueryResourceReference;
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

	@Inject
	private MapViewDAO viewDAO;

	private MapViewFilter filter;

	private long viewId;

	public MusicPage(PageParameters params) {
		super("Beholder FM");

		StringValue identifier = params.get("identifier");
		if (identifier.isNull() || identifier.isEmpty()) {
			throw new RestartResponseAtInterceptPageException(HomePage.class);
		}

		this.filter = new MapViewFilter();
		filter.identifier().set(identifier.toOptionalString());

		MapView currentView = viewDAO.getUniqueByFilter(filter);
		if (currentView == null) {
			throw new RestartResponseAtInterceptPageException(HomePage.class);
		}

		viewId = currentView.getId();

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

		HeaderItem wicketEvent = JavaScriptHeaderItem
				.forReference(WicketEventJQueryResourceReference.get());
		HeaderItem wicketWebsocket = JavaScriptHeaderItem.forReference(WicketWebSocketJQueryResourceReference.get());

		response.render(wicketEvent);
		response.render(wicketWebsocket);

		response.render(JavaScriptHeaderItem
				.forReference(new JavaScriptResourceReference(MapCanvas.class,
						"js/musicplayer.js") {
					@Override
					public List<HeaderItem> getDependencies() {
						return ImmutableList.of(wicketEvent, wicketWebsocket);
					}
				}));
	}
}
