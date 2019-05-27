package com.jeroensteenbeeke.topiroll.beholder.web.components;

import com.jeroensteenbeeke.hyperion.data.DomainObject;
import com.jeroensteenbeeke.hyperion.webcomponents.core.TypedPanel;
import org.apache.wicket.model.IModel;

public abstract class DMViewPanel<T extends DomainObject> extends TypedPanel<T> {
	private static final long serialVersionUID = 3364544432600465989L;

	protected DMViewPanel(String id) {
		super(id);
		setOutputMarkupPlaceholderTag(true);
	}

	protected DMViewPanel(String id, IModel<T> model) {
		super(id, model);
		setOutputMarkupPlaceholderTag(true);
	}
}
