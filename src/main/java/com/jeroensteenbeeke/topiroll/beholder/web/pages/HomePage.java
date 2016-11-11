package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import com.jeroensteenbeeke.hyperion.heinlein.web.pages.BootstrapBasePage;
import com.jeroensteenbeeke.hyperion.social.web.components.slack.SlackLink;

public class HomePage extends BootstrapBasePage {
	private static final long serialVersionUID = 1L;

	public HomePage() {
		super("Beholder");
		
		add(new SlackLink("slack"));
	}
	
	
}