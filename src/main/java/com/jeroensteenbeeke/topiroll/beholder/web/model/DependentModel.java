package com.jeroensteenbeeke.topiroll.beholder.web.model;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

public abstract class DependentModel<D,T> extends LoadableDetachableModel<T> {
	private static final long serialVersionUID = 2583546773047186979L;

	private final IModel<D> referencedModel;

	public DependentModel(IModel<D> referencedModel) {
		this.referencedModel = referencedModel;
	}

	@Override
	protected final T load() {
		return load(referencedModel.getObject());
	}

	protected abstract T load(D object);


	@Override
	public void detach() {
		super.detach();

		referencedModel.detach();
	}
}
