package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import javax.annotation.CheckForNull;

import org.apache.wicket.RestartResponseAtInterceptPageException;

import com.jeroensteenbeeke.hyperion.heinlein.web.pages.BootstrapBasePage;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.EntityPageInitializer;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.web.BeholderSession;
import com.jeroensteenbeeke.topiroll.beholder.web.components.BeholderNavBar;

public abstract class AuthenticatedPage extends BootstrapBasePage implements EntityPageInitializer {
	private static final long serialVersionUID = 1L;

	public AuthenticatedPage(String title) {
		super(title);
		
		add(new BeholderNavBar("navbar"));
		
		if (getUser() == null) {
			BeholderSession.get().invalidate();
			throw new RestartResponseAtInterceptPageException(HomePage.class);
		}
	}
	
	@CheckForNull
	public BeholderUser getUser() {
		return BeholderSession.get().getUser();
	}
}