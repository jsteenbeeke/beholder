package com.jeroensteenbeeke.topiroll.beholder.web.components.combat;

import com.jeroensteenbeeke.hyperion.data.DomainObject;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;
import org.apache.wicket.ajax.AjaxRequestTarget;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;

public interface CombatModeCallback {
	<T extends DomainObject> void createModalWindow(
			@Nonnull
					AjaxRequestTarget target,
			@Nonnull
					BiFunction<String, T,
							CombatModePanel<T>>
					constructor,
			@Nonnull
					T object);

	void redrawTokens(AjaxRequestTarget target);

	TokenInstance getSelectedToken();
}
