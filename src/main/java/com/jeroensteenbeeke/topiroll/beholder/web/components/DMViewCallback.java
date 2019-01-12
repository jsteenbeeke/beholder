package com.jeroensteenbeeke.topiroll.beholder.web.components;

import com.jeroensteenbeeke.hyperion.data.DomainObject;
import com.jeroensteenbeeke.topiroll.beholder.entities.AreaMarker;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;
import org.apache.wicket.ajax.AjaxRequestTarget;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.io.Serializable;

public interface DMViewCallback extends Serializable {
	<T extends DomainObject> void createModalWindow(
			@Nonnull
					AjaxRequestTarget target,
			@Nonnull
					PanelConstructor<T>
					constructor,
			@Nullable
					T object);

	<T extends DomainObject> void createModalWindow(
			@Nonnull
					AjaxRequestTarget target,
			@Nonnull
					WindowConstructor<T>
					constructor,
			@Nullable
					T object);

	void redrawMap(AjaxRequestTarget target);

	void refreshMenus(AjaxRequestTarget target);

	TokenInstance getSelectedToken();

	AreaMarker getSelectedMarker();

	void removeModal(AjaxRequestTarget target);

	Point getClickedLocation();

	Point getPreviousClickedLocation();

	@FunctionalInterface
	public interface PanelConstructor<D extends DomainObject> {
		DMViewPanel<D> apply(String id, D object, DMViewCallback callback);
	}

	@FunctionalInterface
	public interface WindowConstructor<D extends DomainObject> {
		DMModalWindow<D> apply(String id, D object, DMViewCallback callback);
	}
}
