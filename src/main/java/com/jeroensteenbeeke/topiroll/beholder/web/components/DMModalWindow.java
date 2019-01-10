package com.jeroensteenbeeke.topiroll.beholder.web.components;

import com.jeroensteenbeeke.hyperion.heinlein.web.components.BootstrapModalWindow;
import org.apache.wicket.model.IModel;

public class DMModalWindow<T> extends BootstrapModalWindow {

	private static final long serialVersionUID = 3873220497150255788L;

	protected DMModalWindow(String id, String title) {
		super(id, title);
		setOutputMarkupPlaceholderTag(true);

		getDialog().setMarkupId("combat-modal");
	}

	protected DMModalWindow(String id, IModel<T> model, String title) {
		super(id, model, title);
		setOutputMarkupPlaceholderTag(true);

		getDialog().setMarkupId("combat-modal");
	}

	@SuppressWarnings("unchecked")
	public T getModelObject() {
		return (T) getDefaultModelObject();
	}

	public void setModelObject(T object) {
		setDefaultModelObject(object);
	}

	@SuppressWarnings("unchecked")
	public IModel<T> getModel() {
		return (IModel<T>) getDefaultModel();
	}

	public void setModel(IModel<T> model) {
		setDefaultModel(model);
	}
}
