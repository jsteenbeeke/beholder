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
package com.jeroensteenbeeke.topiroll.beholder.web.components;

import javax.inject.Inject;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.protocol.ws.api.WebSocketBehavior;
import org.apache.wicket.protocol.ws.api.message.ClosedMessage;
import org.apache.wicket.protocol.ws.api.message.ConnectedMessage;
import org.apache.wicket.request.UrlUtils;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import com.jeroensteenbeeke.topiroll.beholder.BeholderRegistry;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapRenderers;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;

public class MapCanvas extends WebComponent {
	private static final long serialVersionUID = 1L;

	@Inject
	private MapRenderers renderers;

	private IModel<MapView> viewModel;

	private final boolean previewMode;

	private final long viewId;

	public MapCanvas(String id, IModel<MapView> viewModel,
			boolean previewMode) {
		super(id);
		setOutputMarkupId(true);
		setMarkupId("map");
		this.viewModel = viewModel;
		this.previewMode = previewMode;
		this.viewId = viewModel.getObject().getId();

		add(AttributeModifier.replace("style",
				new LoadableDetachableModel<String>() {
					private static final long serialVersionUID = 1L;

					@Override
					protected String load() {
						return String.format("background-image: url('%s');",
								UrlUtils.rewriteToContextRelative(
										"img/fog-of-war.png",
										getRequestCycle()));
					}
				}));

		// Hack
		add(AttributeModifier.replace("id",
				new LoadableDetachableModel<String>() {
					private static final long serialVersionUID = 1L;

					@Override
					protected String load() {
						return getMarkupId();
					}
				}));

	}

	@Override
	protected void onInitialize() {

		super.onInitialize();

		add(new WebSocketBehavior() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onClose(ClosedMessage message) {
				super.onClose(message);
				
				BeholderRegistry.instance.removeSession(message.getSessionId(), message.getKey());
			}

			@Override
			protected void onConnect(ConnectedMessage message) {
				super.onConnect(message);

				if (previewMode) {
					BeholderRegistry.instance
							.addPreviewSession(message.getSessionId())
							.withKey(message.getKey()).forView(viewId);
				} else {
					BeholderRegistry.instance
							.addLiveSession(message.getSessionId())
							.withKey(message.getKey()).forView(viewId);
				}
			}

		});
	}

	@Override
	protected void onComponentTag(ComponentTag tag) {
		checkComponentTag(tag, "canvas");

	}

	@Override
	protected void onDetach() {
		super.onDetach();

		viewModel.detach();
	}

	@Override
	public void renderHead(IHeaderResponse response) {

		super.renderHead(response);

		response.render(JavaScriptHeaderItem
				.forReference(new JavaScriptResourceReference(MapCanvas.class,
						"js/marker.js")));
		response.render(JavaScriptHeaderItem
				.forReference(new JavaScriptResourceReference(MapCanvas.class,
						"js/token.js")));
		response.render(JavaScriptHeaderItem
				.forReference(new JavaScriptResourceReference(MapCanvas.class,
						"js/map.js")));
		response.render(JavaScriptHeaderItem
				.forReference(new JavaScriptResourceReference(MapCanvas.class,
						"js/renderer.js")));

	}
}
