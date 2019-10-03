package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.play.state;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

public class LocationClickedState implements IMapViewState {

	private static final long serialVersionUID = 3637551757897107725L;

	private final Point clickedLocation;

	private final Point previousClickedLocation;

	LocationClickedState(@Nonnull Point clickedLocation, @Nullable Point previousClickedLocation) {
		this.clickedLocation = clickedLocation;
		this.previousClickedLocation = previousClickedLocation;
	}

	@Override
	public IMapViewState onLocationClicked(Point clickedLocation) {
		return new LocationClickedState(clickedLocation, this.clickedLocation);
	}

	public Point getClickedLocation() {
		return clickedLocation;
	}

	public Point getPreviousClickedLocation() {
		return previousClickedLocation;
	}

	@Override
	public <T> T visit(IMapViewStateVisitor<T> visitor) {
		return visitor.visit(this);
	}

	@Override
	public void detach() {

	}
}
