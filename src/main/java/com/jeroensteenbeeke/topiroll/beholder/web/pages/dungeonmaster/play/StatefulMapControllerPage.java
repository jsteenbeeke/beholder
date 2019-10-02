package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.play;

import com.jeroensteenbeeke.hyperion.data.DomainObject;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.BootstrapBasePage;
import com.jeroensteenbeeke.topiroll.beholder.entities.AreaMarker;
import com.jeroensteenbeeke.topiroll.beholder.entities.DungeonMasterNote;
import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeParticipant;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMModalWindow;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewCallback;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.play.state.*;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public abstract class StatefulMapControllerPage extends BootstrapBasePage implements IMapViewState, DMViewCallback {
	private static final long serialVersionUID = 8934327765672303005L;

	public static final String MODAL_ID = "modal";

	private IMapViewState mapViewState;

	private Component modal;

	protected boolean disableClickListener;

	/**
	 * Create a new webpage with the given title
	 *
	 * @param title The title of the page, will be placed in a {@code <title></title>} tag of the HTML {@code
	 *              <head></head>} section
	 */
	protected StatefulMapControllerPage(String title) {
		super(title);

		this.mapViewState = new EmptyState();
		add(modal = new WebMarkupContainer(MODAL_ID));
		modal.setOutputMarkupPlaceholderTag(true);
		modal.setVisible(false);
	}

	@Override
	public IMapViewState onLocationClicked(Point clickedLocation) {
		return setState(mapViewState.onLocationClicked(clickedLocation));
	}

	@Override
	public IMapViewState onTokenClicked(TokenInstance token) {
		return setState(mapViewState.onTokenClicked(token));
	}

	@Override
	public IMapViewState onParticipantClicked(InitiativeParticipant participant) {
		return setState(mapViewState.onParticipantClicked(participant));
	}

	@Override
	public IMapViewState onNoteClicked(DungeonMasterNote note) {
		return setState(mapViewState.onNoteClicked(note));
	}

	@Override
	public IMapViewState onAreaMarkerClicked(AreaMarker marker) {
		return setState(mapViewState.onAreaMarkerClicked(marker));
	}

	private IMapViewState setState(@Nonnull IMapViewState newState) {
		this.mapViewState.detach();
		this.mapViewState = newState;

		return newState;
	}

	protected void resetState() {
		setState(new EmptyState());
	}

	@Override
	public <T> T visit(IMapViewStateVisitor<T> visitor) {
		return mapViewState.visit(visitor);
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		mapViewState.detach();
		this.disableClickListener = false;
	}

	public final void refreshMenus(@Nullable AjaxRequestTarget target) {
		if (target != null) {
			getMenuComponents().forEach(target::add);
		}
	}

	protected abstract List<Component> getMenuComponents();


	@Override
	public <T extends DomainObject> void createModalWindow(
		@Nonnull
			AjaxRequestTarget target,
		@Nonnull
			PanelConstructor<T> constructor,
		@Nullable
			T object) {
		disableClickListener = true;
		Component oldModal = modal;
		try {
			oldModal.replaceWith(modal = constructor.apply(MODAL_ID, object, this));
			target.add(modal);
			target.appendJavaScript("$('#combat-modal').modal('show');");
		} catch (DMModalWindow.CannotCreateModalWindowException e) {
			// Silent ignore. This exception is a way to abort creating the window when encountering
			// inconsistent state
		}


	}

	@Override
	public <T extends DomainObject> void createModalWindow(@Nonnull AjaxRequestTarget target, @Nonnull WindowConstructor<T> constructor, @Nullable T object) {
		disableClickListener = true;
		Component oldModal = modal;
		try {
			oldModal.replaceWith(modal = constructor.apply(MODAL_ID, object, this));
			target.add(modal);
			target.appendJavaScript("$('#combat-modal').modal('show');");
		} catch (DMModalWindow.CannotCreateModalWindowException e) {
			// Silent ignore. This exception is a way to abort creating the window when encountering
			// inconsistent state
		}

	}

	@Override
	public void removeModal(AjaxRequestTarget target) {
		Component oldModal = modal;
		oldModal.replaceWith(modal = new WebMarkupContainer(MODAL_ID)
			.setOutputMarkupPlaceholderTag(true)
			.setVisible(false));
		target.add(modal);
	}

	@Override
	public TokenInstance getSelectedToken() {
		return visit(new MapViewStateVisitorAdapter<>() {
			@Override
			public TokenInstance visit(TokenInstanceClickedState tokenInstanceClickedState) {
				return tokenInstanceClickedState.getEntity();
			}
		});
	}

	@Override
	public AreaMarker getSelectedMarker() {
		return visit(new MapViewStateVisitorAdapter<>() {
			@Override
			public AreaMarker visit(AreaMarkerClickedState areaMarkerClickedState) {
				return areaMarkerClickedState.getEntity();
			}
		});
	}

	@Override
	public Optional<Point> getClickedLocation() {
		return Optional.ofNullable(visit(new MapViewStateVisitorAdapter<>() {
			@Override
			public Point visit(LocationClickedState locationClickedState) {
				return locationClickedState.getClickedLocation();
			}
		}));
	}

	@Override
	public Optional<Point> getPreviousClickedLocation() {
		return Optional.ofNullable(visit(new MapViewStateVisitorAdapter<>() {
			@Override
			public Point visit(LocationClickedState locationClickedState) {
				return locationClickedState.getPreviousClickedLocation();
			}
		}));
	}
}
