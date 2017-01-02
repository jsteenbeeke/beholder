package com.jeroensteenbeeke.topiroll.beholder.web.components;

import javax.inject.Inject;

import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;

import com.jeroensteenbeeke.topiroll.beholder.beans.URLService;

public class LegalPanel extends Panel {
	private static final long serialVersionUID = 1L;
	
	@Inject
	private URLService urlService;
	
	public LegalPanel(String id) {
		super(id);
		
		add(new ExternalLink("link", urlService.getSourceURL()));
	}
}
