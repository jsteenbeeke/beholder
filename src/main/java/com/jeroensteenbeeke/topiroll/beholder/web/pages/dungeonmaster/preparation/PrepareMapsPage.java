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
package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.preparation;

import com.jeroensteenbeeke.hyperion.heinlein.web.pages.entity.BSEntityFormPage;
import com.jeroensteenbeeke.hyperion.webcomponents.core.form.choice.LambdaRenderer;
import com.jeroensteenbeeke.topiroll.beholder.dao.MapFolderDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.MapFolderFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.ScaledMapFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.components.MapOverviewPanel;
import com.jeroensteenbeeke.topiroll.beholder.web.model.CampaignsModel;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.AuthenticatedPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.PrepareSessionPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.UploadMapStep1Page;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.ViewFolderPage;
import io.vavr.control.Option;
import org.apache.wicket.markup.html.link.Link;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class PrepareMapsPage extends AuthenticatedPage {

	private static final long serialVersionUID = 1L;


	@Inject
	private MapFolderDAO mapFolderDAO;


	public PrepareMapsPage() {
		super("Prepare maps");

		Option<Campaign> activeCampaign = user().flatMap(BeholderUser::activeCampaign);
		if (activeCampaign.isDefined()) {
			warn(String.format("Only showing folders and maps that are tied to the currently active campaign (%s) or not campaign-specific", activeCampaign.map(Campaign::getName).get()));
		}

		add(new MapOverviewPanel("maps", getUser()) {
			private static final long serialVersionUID = 4157905527663457139L;

			@Override
			protected void decorateFolderFilter(
				@Nonnull
					MapFolderFilter folderFilter) {
				folderFilter.parent().isNull();
			}

			@Override
			protected void decorateMapFilter(
				@Nonnull
					ScaledMapFilter mapFilter) {
				mapFilter.folder().isNull();
			}
		});


		add(new Link<ScaledMap>("addmap") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new UploadMapStep1Page(null));

			}
		});


		add(new Link<MapFolder>("addfolder") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new BSEntityFormPage<>(
					create(new MapFolder()).onPage("Create Folder").using(mapFolderDAO)) {

					private static final long serialVersionUID = -6729242615987686357L;

					@Override
					protected void onBeforeSave(MapFolder entity) {
						super.onBeforeSave(entity);
						user().peek(entity::setOwner);
					}

					@Override
					protected void onSaved(MapFolder entity) {
						setResponsePage(new ViewFolderPage(entity));
					}

					@Override
					protected void onCancel(MapFolder entity) {
						setResponsePage(new PrepareSessionPage());
					}
				}.setChoicesModel(MapFolder_.campaign, new CampaignsModel())
				 .setRenderer(MapFolder_.campaign, LambdaRenderer.of(Campaign::getName)));
			}
		});

		add(new Link<Void>("back") {
			private static final long serialVersionUID = 6407103598598058000L;

			@Override
			public void onClick() {
				setResponsePage(new PrepareSessionPage());
			}
		});
	}

}
