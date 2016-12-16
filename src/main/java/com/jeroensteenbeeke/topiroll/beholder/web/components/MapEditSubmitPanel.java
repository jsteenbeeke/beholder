package com.jeroensteenbeeke.topiroll.beholder.web.components;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.panel.Panel;

import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.AddCircleFogOfWarPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.AddRectFogOfWarPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.AddTriangleFogOfWarPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.CreateGroupPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.ViewMapPage;

public class MapEditSubmitPanel extends Panel {
	private static final long serialVersionUID = 1L;

	public MapEditSubmitPanel(String id, Form<ScaledMap> form) {
		super(id);
	
		add(new SubmitLink("submit", form) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onAfterSubmit() {
				super.onAfterSubmit();
				
				if (form.getFeedbackMessages().isEmpty()) {
					setResponsePage(new ViewMapPage(form.getModelObject()));
				}
			}
		});
		
		add(new SubmitLink("submitAndAddCircle", form) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onAfterSubmit() {
				super.onAfterSubmit();
				
				if (form.getFeedbackMessages().isEmpty()) {
					setResponsePage(new AddCircleFogOfWarPage(form.getModelObject()));
				}
			}
		});
		
		add(new SubmitLink("submitAndAddRect", form) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onAfterSubmit() {
				super.onAfterSubmit();
				
				if (form.getFeedbackMessages().isEmpty()) {
					setResponsePage(new AddRectFogOfWarPage(form.getModelObject()));
				}
			}
		});
		
		add(new SubmitLink("submitAndAddTriangle", form) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onAfterSubmit() {
				super.onAfterSubmit();
				
				if (form.getFeedbackMessages().isEmpty()) {
					setResponsePage(new AddTriangleFogOfWarPage(form.getModelObject()));
				}
			}
		});
		
		add(new SubmitLink("submitAndCreateGroup", form) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onAfterSubmit() {
				super.onAfterSubmit();
				
				if (form.getFeedbackMessages().isEmpty()) {
					setResponsePage(new CreateGroupPage(form.getModelObject()));
				}
			}
		});
	}

}
