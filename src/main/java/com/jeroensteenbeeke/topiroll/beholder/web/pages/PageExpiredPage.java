package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import com.jeroensteenbeeke.hyperion.heinlein.web.pages.BootstrapBasePage;
import com.jeroensteenbeeke.hyperion.rollbar.RollBarReference;
import com.jeroensteenbeeke.topiroll.beholder.web.BeholderSession;
import com.jeroensteenbeeke.topiroll.beholder.web.components.LegalPanel;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.OverviewPage;
import org.apache.wicket.markup.html.link.Link;

public class PageExpiredPage extends BootstrapBasePage {
	private static final long serialVersionUID = 1653313737281856944L;

	public PageExpiredPage() {
		super("Page expired");

		add(new Link<Void>("continue") {
			private static final long serialVersionUID = -4236878142549881879L;

			@Override
			public void onClick() {
				if (BeholderSession.get().getUser() != null) {
					setResponsePage(OverviewPage.class);
				} else {
					setResponsePage(HomePage.class);
				}
			}
		});

		add(new LegalPanel("legal"));

	}
}
