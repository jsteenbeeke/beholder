/*
 * This file is part of Beholder
 * Copyright (C) 2016 - 2023 Jeroen Steenbeeke
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
package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster;

import com.jeroensteenbeeke.hyperion.heinlein.web.pages.entity.BSEntityFormPage;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.hyperion.webcomponents.core.form.choice.LambdaRenderer;
import com.jeroensteenbeeke.lux.ActionResult;
import com.jeroensteenbeeke.topiroll.beholder.dao.MapFolderDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.MapFolderFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.ScaledMapFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.components.MapOverviewPanel;
import com.jeroensteenbeeke.topiroll.beholder.web.model.CampaignsModel;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.preparation.PrepareMapsPage;
import io.vavr.control.Option;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;

import org.jetbrains.annotations.NotNull;
import jakarta.inject.Inject;

public class ViewFolderPage extends AuthenticatedPage {

	private static final long serialVersionUID = 1L;

	@Inject
	private MapFolderDAO mapFolderDAO;

	private IModel<MapFolder> folderModel;

	public ViewFolderPage(@NotNull MapFolder folder) {
		super("View folder - ".concat(folder.getName()));

		Option<Campaign> activeCampaign = user().flatMap(BeholderUser::activeCampaign);
		if (activeCampaign.isDefined()) {
			warn(String.format("Only showing folders and maps that are tied to the currently active campaign (%s) or not campaign-specific", activeCampaign.map(Campaign::getName).get()));
		}


		this.folderModel = ModelMaker.wrap(folder);

		add(new MapOverviewPanel("maps", getUser()) {
			private static final long serialVersionUID = -1554085305843937316L;

			@Override
			protected void decorateFolderFilter(@NotNull MapFolderFilter folderFilter) {
				folderFilter.parent(folderModel.getObject());
			}

			@Override
			protected void decorateMapFilter(@NotNull ScaledMapFilter mapFilter) {
				mapFilter.folder(folderModel.getObject());
			}
		});

		add(new Link<ScaledMap>("back") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				MapFolder parent = folderModel.getObject().getParent();
				if (parent == null) {
					setResponsePage(new PrepareMapsPage());
				} else {
					setResponsePage(new ViewFolderPage(parent));
				}

			}
		});

		add(new Link<ScaledMap>("addmap") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new UploadMapStep1Page(folderModel.getObject()));

			}
		});

		add(new Link<MapFolder>("addfolder") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				MapFolder entity = new MapFolder();

				entity.setCampaign(folderModel.getObject().getCampaign());

				BSEntityFormPage<MapFolder> createPage = new BSEntityFormPage<>(
					create(entity).onPage("Create Folder").using(mapFolderDAO)) {

					private static final long serialVersionUID = 7947554551014544459L;

					@Override
					protected void onBeforeSave(MapFolder entity) {
						super.onBeforeSave(entity);
						entity.setParent(folderModel.getObject());
						entity.setCampaign(folderModel.getObject().getCampaign());
						user().peek(entity::setOwner);
					}

					@Override
					protected void onSaved(MapFolder entity) {
						setResponsePage(new ViewFolderPage(entity));
					}

					@Override
					protected void onCancel(MapFolder entity) {
						setResponsePage(new ViewFolderPage(folderModel.getObject()));
					}
				}
					.setChoicesModel(MapFolder_.campaign, new CampaignsModel())
					.setRenderer(MapFolder_.campaign, LambdaRenderer.of(Campaign::getName));

				if (entity.getCampaign() != null) {
					createPage.setReadOnly(MapFolder_.campaign);
				}

				setResponsePage(createPage);
			}
		});
	}

	@Override
	protected void onDetach() {
		super.onDetach();

		folderModel.detach();
	}
}
