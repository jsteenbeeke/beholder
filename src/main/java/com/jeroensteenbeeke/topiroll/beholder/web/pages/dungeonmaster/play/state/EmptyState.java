package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.play.state;

public class EmptyState implements IMapViewState {

	private static final long serialVersionUID = 256695411497313849L;

	@Override
	public <T> T visit(IMapViewStateVisitor<T> visitor) {
		return visitor.visit(this);
	}

	@Override
	public void detach() {

	}
}
