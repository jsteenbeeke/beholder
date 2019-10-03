package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.play.state;

import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;

public class TokenInstanceClickedState extends EntityClickedState<TokenInstance> {
	private static final long serialVersionUID = 1L;

	TokenInstanceClickedState(TokenInstance instance) {
		super(instance);
	}

	@Override
	public <T> T visit(IMapViewStateVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
