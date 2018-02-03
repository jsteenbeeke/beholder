package com.jeroensteenbeeke.topiroll.beholder.web.components.combat;

import com.jeroensteenbeeke.hyperion.data.DomainObject;
import com.jeroensteenbeeke.topiroll.beholder.entities.AreaMarker;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;
import org.apache.wicket.ajax.AjaxRequestTarget;

import javax.annotation.Nonnull;
import java.awt.*;

public interface CombatModeCallback {
	<T extends DomainObject> void createModalWindow(
			@Nonnull
					AjaxRequestTarget target,
			@Nonnull
					PanelConstructor<T>
					constructor,
			@Nonnull
					T object);

	void redrawTokens(AjaxRequestTarget target);

	TokenInstance getSelectedToken();

	AreaMarker getSelectedMarker();

	void removeModal(AjaxRequestTarget target);

	Point getClickedLocation();

	@FunctionalInterface
	public interface PanelConstructor<D extends DomainObject> {
		CombatModePanel<D> apply(String id, D object, CombatModeCallback callback);
	}
}
