package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import org.apache.wicket.RestartResponseAtInterceptPageException;

import com.jeroensteenbeeke.hyperion.heinlein.web.pages.BootstrapBasePage;
import com.jeroensteenbeeke.hyperion.social.web.components.slack.SlackLink;
import com.jeroensteenbeeke.topiroll.beholder.web.BeholderSession;

public class HomePage extends BootstrapBasePage {
	private static final long serialVersionUID = 1L;

	public HomePage() {
		super("Beholder");
		
		if (BeholderSession.get().getUser() != null) {
			throw new RestartResponseAtInterceptPageException(new OverviewPage());
		}
		
		add(new SlackLink("slack"));
	}
	
	
}