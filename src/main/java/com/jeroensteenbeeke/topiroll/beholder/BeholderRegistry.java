package com.jeroensteenbeeke.topiroll.beholder;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.protocol.ws.api.registry.IKey;

import com.fasterxml.jackson.databind.ObjectMapper;

public enum BeholderRegistry {
	instance;

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

	public static class RegistryEntry {
		private final IKey key;

		private final long viewId;

		private final boolean previewMode;

		private RegistryEntry(IKey key, long viewId, boolean previewMode) {
			this.key = key;
			this.viewId = viewId;
			this.previewMode = previewMode;
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

		public AddViewStep withKey(IKey key) {
			return new AddViewStep(id, previewMode, key, registry);
		}
	}

	public static class AddViewStep {
		private final String id;

		private final IKey key;

		private final BeholderRegistry registry;

		private final boolean previewMode;

		private AddViewStep(String id, boolean previewMode, IKey key,
				BeholderRegistry registry) {
			this.id = id;
			this.previewMode = previewMode;
			this.key = key;
			this.registry = registry;
		}

		public void forView(long viewId) {
			RegistryEntry entry = new RegistryEntry(key, viewId, previewMode);

			registry.entries.put(id, entry);
		}
	}
}
