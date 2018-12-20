package com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.combat;

import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.TokenInstanceDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenBorderType;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewCallback;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.LoadableDetachableModel;

import javax.inject.Inject;
import java.util.Optional;

public class TokenStatusPanel extends DMViewPanel<MapView> {
	@Inject
	private MapService mapService;

	public TokenStatusPanel(String id, DMViewCallback callback) {
		super(id);

		add(new Label("name", new LoadableDetachableModel<String>() {
			@Override
			protected String load() {
				return Optional.ofNullable(callback.getSelectedToken()).map(TokenInstance::getBadge)
						.orElse("-");
			}
		}));

		add(new Label("hp", new LoadableDetachableModel<String>() {
			@Override
			protected String load() {
				return Optional.ofNullable(callback.getSelectedToken()).filter(i -> i.getCurrentHitpoints() != null && i.getMaxHitpoints() != null)
						.map(i -> String.format("%d/%d HP", i.getCurrentHitpoints(), i.getMaxHitpoints()))
						.orElse("-");
			}
		}));

		add(new AjaxLink<TokenInstance>("damage") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				callback.createModalWindow(target, ApplyTokenDamageWindow::new, callback
						.getSelectedToken());
			}

			@Override
			public boolean isVisible() {
				return super.isVisible() && Optional.ofNullable(callback.getSelectedToken()).map
						(TokenInstance::getMaxHitpoints).isPresent();
			}
		});

		add(new AjaxLink<TokenInstance>("ally") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				mapService.setTokenBorderType(callback
						.getSelectedToken(), TokenBorderType.Ally);
				callback.redrawMap(target);
			}

			@Override
			public boolean isVisible() {
				return super.isVisible() && Optional.ofNullable(callback.getSelectedToken())
													.filter(t -> t
															.getBorderType()
															!= TokenBorderType.Ally).isPresent();
			}
		});

		add(new AjaxLink<TokenInstance>("neutral") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				mapService.setTokenBorderType(callback.getSelectedToken(), TokenBorderType
						.Neutral);
				callback.redrawMap(target);

			}

			@Override
			public boolean isVisible() {
				return super.isVisible() && Optional.ofNullable(callback.getSelectedToken())
													.filter(t -> t
															.getBorderType()
															!= TokenBorderType.Neutral).isPresent();
			}
		});

		add(new AjaxLink<TokenInstance>("enemy") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				mapService.setTokenBorderType(callback.getSelectedToken(), TokenBorderType.Enemy);
				callback.redrawMap(target);

			}

			@Override
			public boolean isVisible() {
				return super.isVisible() && Optional.ofNullable(callback.getSelectedToken())
													.filter(t -> t
															.getBorderType()
															!= TokenBorderType.Enemy).isPresent();
			}
		});

		add(new AjaxLink<TokenInstance>("hide") {
			@Override
			public void onClick(AjaxRequestTarget target) {

				TokenInstance token = callback.getSelectedToken();
				if (token.isShow()) {
					mapService.hideToken(token);
				} else {
					mapService.showToken(token);
				}
				callback.redrawMap(target);

			}

		
		}.setBody(new LoadableDetachableModel<String>() {
			@Override
			protected String load() {
				TokenInstance token = callback.getSelectedToken();
				if (token.isShow()) {
					return "Hide";
				} else {
					return "Reveal";
				}
			}
		}));

		add(new AjaxLink<TokenInstance>("delete") {
			@Inject
			private TokenInstanceDAO instanceDAO;

			@Override
			public void onClick(AjaxRequestTarget target) {

				TokenInstance token = callback.getSelectedToken();
				instanceDAO.delete(token);
				callback.redrawMap(target);

			}


		});

	}

}
