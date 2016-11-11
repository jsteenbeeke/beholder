package com.jeroensteenbeeke.topiroll.beholder.beans;

import java.io.Serializable;

import org.apache.wicket.Page;

public interface MenuItemProvider extends Serializable {
	String getLabel();
	
	default Serializable getBadge() {
		return null;
	}
	
	Page onClick();
	
	int getPriority();
	
	boolean isSelected(Page currentPage);

	default boolean isBadgeSupported() {
		return false;
	}
}
