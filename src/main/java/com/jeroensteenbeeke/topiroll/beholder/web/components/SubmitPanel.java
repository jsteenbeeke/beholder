package com.jeroensteenbeeke.topiroll.beholder.web.components;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.danekja.java.util.function.serializable.SerializableConsumer;

import com.googlecode.wicket.jquery.ui.markup.html.link.SubmitLink;

public class SubmitPanel<T> extends Panel {
	private static final long serialVersionUID = 1L;

	public SubmitPanel(String id, Form<T> form, SerializableConsumer<T> onAfterSubmit) {
		super(id);

		SubmitLink submitLink = new SubmitLink("submit", form) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onAfterSubmit() {
				super.onAfterSubmit();
				
				onAfterSubmit.accept(form.getModelObject());
			}
		};
		
		decorateLink(submitLink);
		
		add(submitLink);
	}

	protected void decorateLink(SubmitLink submitLink) {
		
	}
	
	

}
