package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import com.jeroensteenbeeke.hyperion.heinlein.web.pages.BootstrapBasePage;
import com.jeroensteenbeeke.topiroll.beholder.web.BeholderSession;
import com.jeroensteenbeeke.topiroll.beholder.web.components.LegalPanel;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.OverviewPage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.http.WebResponse;

import javax.servlet.http.HttpServletResponse;

public class InternalErrorPage extends BootstrapBasePage {

	private static final long serialVersionUID = 8771238347909522153L;

	public InternalErrorPage() {
		super("Internal error");

		add(new Link<Void>("continue") {
			private static final long serialVersionUID1 = -153459368902727914L;

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

	@Override
	protected void setHeaders(final WebResponse response)
	{
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}

	@Override
	public boolean isErrorPage() {
		return true;
	}

	@Override
	public boolean isVersioned() {
		return false;
	}
}
