package com.jeroensteenbeeke.topiroll.beholder.util;

import javax.annotation.Nonnull;

import org.apache.wicket.ajax.AjaxRequestTarget;

public class AjaxRequestTargetJavaScriptHandler implements JavaScriptHandler {
	private final AjaxRequestTarget target;

	public AjaxRequestTargetJavaScriptHandler(
			@Nonnull AjaxRequestTarget target) {
		this.target = target;
	}

	@Override
	public void execute(String script) {
		target.appendJavaScript(script);
	}

}
