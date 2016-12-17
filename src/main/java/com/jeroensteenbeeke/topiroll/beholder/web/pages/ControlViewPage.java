/**
 * This file is part of Beholder
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;

import com.jeroensteenbeeke.hyperion.heinlein.web.resources.TouchPunchJavaScriptReference;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.web.components.HideRevealController;
import com.jeroensteenbeeke.topiroll.beholder.web.components.MapCanvas;
import com.jeroensteenbeeke.topiroll.beholder.web.components.MapSelectController;

public class ControlViewPage extends AuthenticatedPage {

	private static final String CONTROLLER_ID = "controller";

	private static final long serialVersionUID = 1L;

	private IModel<MapView> viewModel;

	private WebMarkupContainer controller;

	public ControlViewPage(MapView view) {
		super(String.format("Control View - %s", view.getIdentifier()));

		viewModel = ModelMaker.wrap(view);
		add(new MapCanvas("preview", viewModel, true));

		add(new Link<Void>("back") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(OverviewPage.class);
			}
		});

		if (view.getSelectedMap() == null) {
			add(controller = new MapSelectController(CONTROLLER_ID, getUser(),
					view) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onMapSelected(@Nullable ScaledMap map,
						@Nonnull AjaxRequestTarget target) {
					if (map != null) {
						WebMarkupContainer newController = new HideRevealController(
								CONTROLLER_ID, viewModel.getObject(), map);
						controller.replaceWith(newController);
						target.add(newController);
						controller = newController;
					}
				}
			});
		} else {
			add(controller = new HideRevealController(CONTROLLER_ID, view,
					view.getSelectedMap()));
		}

		add(new AjaxLink<Void>("mapSelect") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				if (!(controller instanceof MapSelectController)) {
					WebMarkupContainer newController = new MapSelectController(
							CONTROLLER_ID, getUser(), viewModel.getObject()) {
						private static final long serialVersionUID = 1L;

						@Override
						public void onMapSelected(@Nullable ScaledMap map,
								@Nonnull AjaxRequestTarget target) {
							if (map != null) {
								WebMarkupContainer newController = new HideRevealController(
										CONTROLLER_ID, viewModel.getObject(),
										map);
								controller.replaceWith(newController);
								target.add(newController);
								controller = newController;
							}
						}
					};
					controller.replaceWith(newController);
					target.add(newController);
					controller = newController;
				}

			}
		});
		add(new AjaxLink<Void>("hideReveal") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				if (!(controller instanceof HideRevealController)) {
					MapView view = viewModel.getObject();
					ScaledMap map = view.getSelectedMap();

					if (map != null) {
						WebMarkupContainer newController = new HideRevealController(
								CONTROLLER_ID, view, map);
						controller.replaceWith(newController);
						target.add(newController);
						controller = newController;
					}
				}

			}
		});

	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);

		response.render(JavaScriptHeaderItem
				.forReference(TouchPunchJavaScriptReference.get()));
	}
}
