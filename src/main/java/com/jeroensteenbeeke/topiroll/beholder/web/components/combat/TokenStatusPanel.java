package com.jeroensteenbeeke.topiroll.beholder.web.components.combat;

import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenBorderType;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;

import javax.inject.Inject;

public class TokenStatusPanel extends CombatModePanel<MapView> {
	@Inject
	private MapService mapService;

	public TokenStatusPanel(String id, CombatModeCallback callback) {
		super(id);

		add(new AjaxLink<TokenInstance>("damage") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				callback.createModalWindow(target, ApplyTokenDamagePanel::new, callback
						.getSelectedToken());
			}
		});

		add(new AjaxLink<TokenInstance>("ally") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				mapService.setTokenBorderType(callback
						.getSelectedToken(), TokenBorderType.Ally);
				callback.redrawTokens(target);
			}

			@Override
			public boolean isVisible() {
				return super.isVisible() && callback.getSelectedToken().getBorderType() != TokenBorderType.Ally;
			}
		});

		add(new AjaxLink<TokenInstance>("neutral") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				mapService.setTokenBorderType(callback.getSelectedToken(), TokenBorderType.Neutral);
				callback.redrawTokens(target);

			}

			@Override
			public boolean isVisible() {
				return super.isVisible() && callback.getSelectedToken().getBorderType() != TokenBorderType
						.Neutral;
			}
		});

		add(new AjaxLink<TokenInstance>("enemy") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				mapService.setTokenBorderType(callback.getSelectedToken(), TokenBorderType.Enemy);
				callback.redrawTokens(target);

			}

			@Override
			public boolean isVisible() {
				return super.isVisible() && callback.getSelectedToken().getBorderType() != TokenBorderType
						.Enemy;
			}
		});

	}

}
