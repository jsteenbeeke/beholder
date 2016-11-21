package com.jeroensteenbeeke.topiroll.beholder.util;

import javax.annotation.Nonnull;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

public class OnDomReadyJavaScriptHandler implements JavaScriptHandler {
	private final IHeaderResponse response;

	public OnDomReadyJavaScriptHandler(@Nonnull IHeaderResponse response) {
		this.response = response;
	}

	@Override
	public void execute(String script) {
		response.render(new OnDomReadyHeaderItem(script));
	}

}
