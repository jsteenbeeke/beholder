package com.jeroensteenbeeke.topiroll.beholder.entities;

public enum VisibilityStatus {
	VISIBLE {
		@Override
		public boolean isVisible(boolean previewMode) {
			return true;
		}
	}, DM_ONLY {
		@Override
		public boolean isVisible(boolean previewMode) {
			return previewMode;
		}
	}, INVISIBLE {
		@Override
		public boolean isVisible(boolean previewMode) {
			return false;
		}
	};
	
	public abstract boolean isVisible(boolean previewMode);
}
