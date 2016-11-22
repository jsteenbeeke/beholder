package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;

public class ViewMapPage extends AuthenticatedPage {
	private static final long serialVersionUID = 1L;

	public ViewMapPage(ScaledMap map) {
		super(String.format("View Map - %s", map.getName()));
	}
}
