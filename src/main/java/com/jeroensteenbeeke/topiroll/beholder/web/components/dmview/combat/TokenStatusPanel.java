/**
 * This file is part of Beholder
 * (C) 2016-2019 Jeroen Steenbeeke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
	private static final long serialVersionUID = 1176280334947526244L;
	@Inject
	private MapService mapService;

	public TokenStatusPanel(String id, DMViewCallback callback) {
		super(id);

		add(new Label("name", new LoadableDetachableModel<String>() {
			private static final long serialVersionUID = 7572052680274915751L;

			@Override
			protected String load() {
				return Optional.ofNullable(callback.getSelectedToken()).map(TokenInstance::getBadge)
						.orElse("-");
			}
		}));

		add(new Label("hp", new LoadableDetachableModel<String>() {
			private static final long serialVersionUID = -7841921550624428490L;

			@Override
			protected String load() {
				return Optional.ofNullable(callback.getSelectedToken()).filter(i -> i.getCurrentHitpoints() != null && i.getMaxHitpoints() != null)
						.map(i -> String.format("%d/%d HP", i.getCurrentHitpoints(), i.getMaxHitpoints()))
						.orElse("-");
			}
		}));

		add(new AjaxLink<TokenInstance>("damage") {
			private static final long serialVersionUID = 544107002986766635L;

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
			private static final long serialVersionUID = 7724047619715323603L;

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
			private static final long serialVersionUID = -4426157222702488176L;

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
			private static final long serialVersionUID = -6751878262372051969L;

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
			private static final long serialVersionUID = 8210417164184943024L;

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
			private static final long serialVersionUID = -1540177023550864910L;

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
			private static final long serialVersionUID = -3699567965528536589L;
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
