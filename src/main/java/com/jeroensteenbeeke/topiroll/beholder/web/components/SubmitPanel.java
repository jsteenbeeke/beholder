package com.jeroensteenbeeke.topiroll.beholder.web.components;

import java.util.function.Consumer;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;

import com.googlecode.wicket.jquery.ui.markup.html.link.SubmitLink;

public class SubmitPanel<T> extends Panel {
	private static final long serialVersionUID = 1L;

	public SubmitPanel(String id, Form<T> form, Consumer<T> onAfterSubmit) {
		super(id);

		add(new SubmitLink("submit", form) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onAfterSubmit() {
				super.onAfterSubmit();
				
				onAfterSubmit.accept(form.getModelObject());
			}
		});
	}
	
	

}
