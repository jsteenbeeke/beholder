package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.play.state;

import com.jeroensteenbeeke.hyperion.data.DomainObject;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import org.apache.wicket.model.IModel;

import javax.annotation.Nonnull;

public abstract class EntityClickedState<T extends DomainObject> implements IMapViewState {
	private static final long serialVersionUID = 1L;

	private IModel<T> entity;

	protected EntityClickedState(@Nonnull T entity) {
		this.entity = ModelMaker.wrap(entity);
	}

	public T getEntity() {
		return entity.getObject();
	}

	@Override
	public void detach() {
		entity.detach();
	}
}
