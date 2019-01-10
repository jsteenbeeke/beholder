/**
 * This file is part of Beholder
 * (C) 2016 Jeroen Steenbeeke
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.jeroensteenbeeke.hyperion.data.DomainObject;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.BootstrapPagingNavigator;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.IconLink;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.entity.BSEntityFormPage;
import com.jeroensteenbeeke.hyperion.icons.fontawesome.FontAwesome;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.hyperion.webcomponents.core.form.choice.LambdaRenderer;
import com.jeroensteenbeeke.topiroll.beholder.BeholderApplication;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.*;
import com.jeroensteenbeeke.topiroll.beholder.web.components.AbstractMapPreview;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.preparation.CreateLinkPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.preparation.PrepareMapsPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.tabletop.MapViewPage;
import io.vavr.collection.Array;
import io.vavr.collection.Seq;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

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

	@Inject
	private MapLinkDAO mapLinkDAO;


	@Inject
	private ScaledMapDAO scaledMapDAO;

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
					setResponsePage(new PrepareMapsPage());
				}

			}
		});

		mapModel = ModelMaker.wrap(map);
		add(new AbstractMapPreview("preview", map, Math.min(1200, map.getBasicWidth())) {
			@Override
			protected void addOnDomReadyJavaScript(String canvasId, StringBuilder js, double factor) {
				getMap().getAllShapes().stream()
						.map(s -> s.visit(new FogOfWarPreviewRenderer(canvasId, factor)))
						.forEach(js::append);
				getMap().getTokens().stream()
						.map(t -> String.format("previewToken(%s, %s);\n", canvasId, t.toPreview(factor)))
						.forEach(js::append);
			}
		});

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

				item.add(new MapLinkView("links", new MapLinkFilter().sourceGroup(group)));

				item.add(new AbstractMapPreview("thumb", map, 128) {
					@Override
					protected void addOnDomReadyJavaScript(String canvasId, StringBuilder js, double factor) {
						item.getModelObject().getShapes().stream()
								.map(s -> s.visit(new FogOfWarPreviewRenderer(canvasId, factor)))
								.forEach(js::append);
					}
				});
				item.add(new IconLink<FogOfWarGroup>("edit", item.getModel(),
						FontAwesome.edit) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {

						setResponsePage(new EditGroupPage(getModelObject()));
					}
				});
				item.add(new IconLink<FogOfWarGroup>("link", item.getModel(),
						FontAwesome.link) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {


						setResponsePage(new CreateLinkPage(getModelObject()));
					}
				});
				item.add(new IconLink<FogOfWarGroup>("delete", item.getModel(),
						FontAwesome.trash) {
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
				item.add(new AbstractMapPreview("thumb", map, 128) {
					private static final long serialVersionUID = 6581347254974917175L;

					@Override
					protected void addOnDomReadyJavaScript(String canvasId, StringBuilder js, double factor) {
						js.append(item.getModelObject().visit(new FogOfWarPreviewRenderer(canvasId, factor)));
					}
				});

				item.add(new IconLink<FogOfWarShape>("delete", item.getModel(),
						FontAwesome.trash) {
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

		DataView<TokenInstance> tokenView =
				new DataView<TokenInstance>("tokens", FilterDataProvider.of(tokenFilter, tokenInstanceDAO)) {

					private static final long serialVersionUID = 1L;

					@Override
					protected void populateItem(Item<TokenInstance> item) {
						TokenInstance instance = item.getModelObject();
						item.add(new Label("token", instance.getLabel()));
						item.add(new Label("location",
								String.format("(%d,%d)", instance.getOffsetX(), instance.getOffsetY())));
						item.add(new IconLink<TokenInstance>("reveal", item.getModel(), FontAwesome.eye) {
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
								FontAwesome.edit) {
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
								page.setChoices(TokenInstance_.borderType, Lists.newArrayList(TokenBorderType.values()));

								setResponsePage(page);

							}
						});
						item.add(new IconLink<TokenInstance>("delete", item.getModel(), FontAwesome.trash) {
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

	public ScaledMap getMap() {
		return mapModel.getObject();
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		mapModel.detach();
	}


	private class MapLinkView extends DataView<MapLink> {
		private static final long serialVersionUID = 28203555029721446L;

		private MapLinkView(String id, MapLinkFilter filter) {
			super(id, FilterDataProvider.of(filter, mapLinkDAO));
		}

		@Override
		protected void populateItem(Item<MapLink> item) {
			MapLink link = item.getModelObject();

			item.add(new Label("map", String.format("%s in %s", link.getTargetGroup().getName(), link.getTargetGroup().getMap().getNameWithFolders())));
			item.add(new IconLink<MapLink>("delete", item.getModel(), FontAwesome.trash) {
				private static final long serialVersionUID = -3453872306526472488L;

				@Override
				public void onClick() {
					mapLinkDAO.delete(getModelObject());

					this.setResponsePage(new ViewMapPage(mapModel.getObject()));
				}
			});
		}
	}
}
