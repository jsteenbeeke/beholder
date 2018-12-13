package com.jeroensteenbeeke.topiroll.beholder.web.components.exploration;

import com.jeroensteenbeeke.hyperion.data.DomainObject;
import com.jeroensteenbeeke.hyperion.webcomponents.core.TypedPanel;
import org.apache.wicket.model.IModel;

public abstract class ExplorationModePanel<T extends DomainObject> extends TypedPanel<T> {
	protected ExplorationModePanel(String id) {
		super(id);
		setOutputMarkupPlaceholderTag(true);
	}

	protected ExplorationModePanel(String id, IModel<T> model) {
		super(id, model);
		setOutputMarkupPlaceholderTag(true);
	}
}
