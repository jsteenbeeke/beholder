package com.jeroensteenbeeke.topiroll.beholder.web.components.combat;

import com.jeroensteenbeeke.hyperion.data.DomainObject;
import com.jeroensteenbeeke.hyperion.webcomponents.core.TypedPanel;
import org.apache.wicket.model.IModel;

public abstract class CombatModePanel<T extends DomainObject> extends TypedPanel<T> {
	protected CombatModePanel(String id) {
		super(id);
		setOutputMarkupPlaceholderTag(true);
	}

	protected CombatModePanel(String id, IModel<T> model) {
		super(id, model);
		setOutputMarkupPlaceholderTag(true);
	}
}
