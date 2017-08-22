/**
 * This file is part of Beholder
 * (C) 2016 Jeroen Steenbeeke
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
package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import java.awt.Graphics2D;

import javax.inject.Inject;

import com.jeroensteenbeeke.topiroll.beholder.entities.*;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;

import com.google.common.collect.Lists;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.BootstrapPagingNavigator;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.GlyphIcon;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.IconLink;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.BSEntityFormPage;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarGroupDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarShapeDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.TokenDefinitionDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.TokenInstanceDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.FogOfWarGroupFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.FogOfWarShapeFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.TokenDefinitionFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.TokenInstanceFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.resources.AbstractFogOfWarPreviewResource;

public class ViewMapPage extends AuthenticatedPage {
	private static final long serialVersionUID = 1L;

	@Inject
	private FogOfWarShapeDAO shapeDAO;

	@Inject
	private FogOfWarGroupDAO groupDAO;

	@Inject
	private MapService mapService;

	@Inject
	private TokenDefinitionDAO tokenDAO;

	@Inject
	private TokenInstanceDAO tokenInstanceDAO;
	
	private IModel<ScaledMap> mapModel;

	public ViewMapPage(ScaledMap map) {
		super(String.format("View Map - %s", map.getName()));

		add(new Link<Void>("back") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				MapFolder folder = mapModel.getObject().getFolder();
				if (folder != null) {
					setResponsePage(new ViewFolderPage(folder));
 				} else {
					setResponsePage(new PrepareSessionPage());
				}

			}
		});

		mapModel = ModelMaker.wrap(map);
		add(new NonCachingImage("preview",
				new AbstractFogOfWarPreviewResource(mapModel) {

					private static final long serialVersionUID = 1L;

					@Override
					public void drawShape(Graphics2D graphics2d) {

					}
				}));

		FogOfWarGroupFilter groupFilter = new FogOfWarGroupFilter();
		groupFilter.map().set(map);
		groupFilter.name().orderBy(true);

		DataView<FogOfWarGroup> groupsView = new DataView<FogOfWarGroup>(
				"groups", FilterDataProvider.of(groupFilter, groupDAO)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<FogOfWarGroup> item) {
				FogOfWarGroup group = item.getModelObject();

				item.add(new Label("name", group.getName()));
				item.add(new NonCachingImage("thumb",
						group.createThumbnailResource(200)));
				item.add(new IconLink<FogOfWarGroup>("edit", item.getModel(),
						GlyphIcon.edit) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {

						setResponsePage(new EditGroupPage(getModelObject()));
					}
				});
				item.add(new IconLink<FogOfWarGroup>("delete", item.getModel(),
						GlyphIcon.trash) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						mapService.ungroup(getModelObject());
						setResponsePage(new ViewMapPage(mapModel.getObject()));
					}
				});
			}
		};
		groupsView.setItemsPerPage(10L);
		add(groupsView);
		add(new BootstrapPagingNavigator("groupnav", groupsView));

		FogOfWarShapeFilter shapeFilter = new FogOfWarShapeFilter();
		shapeFilter.map().set(map);
		shapeFilter.group().isNull();

		DataView<FogOfWarShape> shapesView = new DataView<FogOfWarShape>(
				"shapes", FilterDataProvider.of(shapeFilter, shapeDAO)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<FogOfWarShape> item) {
				FogOfWarShape shape = item.getModelObject();

				item.add(new Label("shape", shape.getDescription()));
				item.add(new NonCachingImage("thumb",
						shape.createThumbnailResource(200)));
				item.add(new IconLink<FogOfWarShape>("delete", item.getModel(),
						GlyphIcon.trash) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						mapService.deleteShape(getModelObject());
						setResponsePage(new ViewMapPage(mapModel.getObject()));
					}
				});
			}

		};
		shapesView.setItemsPerPage(10L);
		add(shapesView);
		add(new BootstrapPagingNavigator("shapenav", shapesView));

		TokenInstanceFilter tokenFilter = new TokenInstanceFilter();
		tokenFilter.map().set(map);
		tokenFilter.badge().orderBy(true);

		DataView<TokenInstance> tokenView = new DataView<TokenInstance>("tokens", FilterDataProvider.of(tokenFilter, tokenInstanceDAO)) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<TokenInstance> item) {
				TokenInstance instance = item.getModelObject();
				item.add(new Label("token", instance.getLabel()));
				item.add(new Label("location", String.format("(%d,%d)", instance.getOffsetX(), instance.getOffsetY())));
				item.add(new IconLink<TokenInstance>("reveal", item.getModel(), GlyphIcon.eyeOpen) {
					private static final long serialVersionUID = 1L;

					@Inject
					private MapService mapService;
					
					@Override
					public void onClick() {
						mapService.showToken(getModelObject());
						setResponsePage(new ViewMapPage(mapModel.getObject()));
					}
				}.setVisible(!instance.isShow()));
				item.add(new IconLink<TokenInstance>("edit", item.getModel(),
						GlyphIcon.edit) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						BSEntityFormPage<TokenInstance> page = new BSEntityFormPage<TokenInstance>(
								edit(getModelObject()).onPage("Edit Token").withoutDelete()
										.using(tokenInstanceDAO)) {

							private static final long serialVersionUID = 1L;

							@Override
							protected void onSaved(TokenInstance entity) {
								setResponsePage(new ViewMapPage(mapModel.getObject()));
							}

							@Override
							protected void onCancel(TokenInstance entity) {
								setResponsePage(new ViewMapPage(mapModel.getObject()));
							}
							
							

						};
						page.setChoices(TokenBorderType.class, Lists.newArrayList(TokenBorderType.values()));
						
						setResponsePage(page);

					}
				});
				item.add(new IconLink<TokenInstance>("delete", item.getModel(), GlyphIcon.trash) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						tokenInstanceDAO.delete(getModelObject());
						setResponsePage(new ViewMapPage(mapModel.getObject()));
					}
				});
				
			}
		};
		add(tokenView);
		add(new BootstrapPagingNavigator("tokennav", tokenView));
		

		add(new Link<ScaledMap>("addcircle", mapModel) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new AddCircleFogOfWarPage(getModelObject()));

			}
		});

		add(new Link<ScaledMap>("addrect", mapModel) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new AddRectFogOfWarPage(getModelObject()));

			}
		});

		add(new Link<ScaledMap>("addtriangle", mapModel) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new AddTriangleFogOfWarPage(getModelObject()));

			}
		});

		add(new Link<ScaledMap>("group", mapModel) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new CreateGroupPage(getModelObject()));

			}
		});

		TokenDefinitionFilter filter = new TokenDefinitionFilter();
		filter.owner().equalTo(getUser());

		add(new Link<ScaledMap>("addtokens", mapModel) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new AddTokenInstance1Page(getModelObject()));

			}
		}.setVisible(tokenDAO.countByFilter(filter) > 0));

	}

	@Override
	protected void onDetach() {
		super.onDetach();
		mapModel.detach();
	}
}
