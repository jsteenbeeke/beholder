package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import org.apache.wicket.markup.html.basic.Label;

import com.jeroensteenbeeke.hyperion.heinlein.web.pages.BootstrapBasePage;

public class SlackErrorPage extends BootstrapBasePage {

	private static final long serialVersionUID = 1L;

	public SlackErrorPage(String error) {
		super("Error");

		add(new Label("error", error));
	}
}
