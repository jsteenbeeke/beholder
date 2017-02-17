package com.jeroensteenbeeke.topiroll.beholder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.apache.wicket.protocol.ws.api.IWebSocketConnection;
import org.apache.wicket.protocol.ws.api.registry.IKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeroensteenbeeke.topiroll.beholder.web.data.JSRenderable;
import com.jeroensteenbeeke.topiroll.beholder.web.data.Payload;

public enum BeholderRegistry {
	instance;

	private static final Logger log = LoggerFactory
			.getLogger(BeholderRegistry.class);

	private static final ObjectMapper mapper = initMapper();

	private static ObjectMapper initMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper;
	}

	private Map<String, RegistryEntry> entries;

	private BeholderRegistry() {
		entries = new HashMap<>();
	}

	public AddKeyStep addLiveSession(String id) {
		return new AddKeyStep(id, false, this);
	}

	public AddKeyStep addPreviewSession(String id) {
		return new AddKeyStep(id, true, this);
	}

	public void removeSession(String id, IKey key) {
		if (entries.containsKey(id)) {
			RegistryEntry entry = entries.get(id);
			if (entry.getKey().equals(key)) {
				entries.remove(id);
			}

		}
	}

	public void sendToView(long viewId, JSRenderable renderable) {
		sendToView(viewId, e -> true, renderable);
	}
	
	public void sendToView(long viewId, Predicate<RegistryEntry> selector, JSRenderable renderable) {

		entries.forEach((sessionId, entry) -> {
			if (entry.getViewId() == viewId && selector.test(entry)) {
				Payload payload = new Payload();
				payload.setCanvasId(entry.getMarkupId());
				payload.setData(renderable);

				IWebSocketConnection connection = BeholderApplication.get()
						.getWebSocketRegistry()
						.getConnection(BeholderApplication.get(), sessionId,
								entry.getKey());
				if (connection != null) {
				try {
					connection.sendMessage(mapper.writeValueAsString(payload));
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
				
				}

			}
		});

	}

	public static class RegistryEntry {
		private final IKey key;
		
		private final String sessionId;

		private final long viewId;

		private final String markupId;

		private final boolean previewMode;

		private RegistryEntry(IKey key, String sessionId, String markupId, long viewId,
				boolean previewMode) {
			this.markupId = markupId;
			this.sessionId = sessionId;
			this.key = key;
			this.viewId = viewId;
			this.previewMode = previewMode;
		}
		
		public String getSessionId() {
			return sessionId;
		}

		public String getMarkupId() {
			return markupId;
		}

		public IKey getKey() {
			return key;
		}

		public long getViewId() {
			return viewId;
		}

		public boolean isPreviewMode() {
			return previewMode;
		}
	}

	public static class AddKeyStep {
		private final String id;

		private final BeholderRegistry registry;

		private final boolean previewMode;

		private AddKeyStep(String id, boolean previewMode,
				BeholderRegistry registry) {
			this.id = id;
			this.previewMode = previewMode;
			this.registry = registry;
		}

		public AddMarkupIdStep withKey(IKey key) {
			return new AddMarkupIdStep(id, previewMode, key, registry);
		}
	}

	public static class AddMarkupIdStep {
		private final String id;

		private final IKey key;

		private final BeholderRegistry registry;

		private final boolean previewMode;

		private AddMarkupIdStep(String id, boolean previewMode, IKey key,
				BeholderRegistry registry) {
			super();
			this.id = id;
			this.key = key;
			this.registry = registry;
			this.previewMode = previewMode;
		}

		public AddViewStep withMarkupId(String markupId) {
			return new AddViewStep(id, markupId, previewMode, key, registry);
		}

	}

	public static class AddViewStep {
		private final String sessionId;

		private final String markupId;

		private final IKey key;

		private final BeholderRegistry registry;

		private final boolean previewMode;

		private AddViewStep(String sessionId, String markupId,
				boolean previewMode, IKey key, BeholderRegistry registry) {
			this.sessionId = sessionId;
			this.markupId = markupId;
			this.previewMode = previewMode;
			this.key = key;
			this.registry = registry;
		}

		public void forView(long viewId) {
			RegistryEntry entry = new RegistryEntry(key, sessionId, markupId, viewId,
					previewMode);

			registry.entries.put(sessionId, entry);
		}
	}
}

