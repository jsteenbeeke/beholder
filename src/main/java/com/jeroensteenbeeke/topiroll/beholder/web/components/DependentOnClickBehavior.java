package com.jeroensteenbeeke.topiroll.beholder.web.components;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

public abstract class DependentOnClickBehavior<T> extends OnClickBehavior {
	private static final long serialVersionUID = 6481092014135303291L;

	private final IModel<T> model;

	public DependentOnClickBehavior(IModel<T> model) {
		this.model = model;
	}

	@Override
	protected void onClick(AjaxRequestTarget target, ClickEvent event) {
		onClick(target, event, model.getObject());
	}

	protected abstract void onClick(AjaxRequestTarget target, ClickEvent event, T modelObject);

	@Override
	public void detach(Component component) {
		super.detach(component);

		model.detach();
	}
}
