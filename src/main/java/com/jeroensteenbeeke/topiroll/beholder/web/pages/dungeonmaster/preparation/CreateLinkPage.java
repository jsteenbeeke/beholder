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

import com.google.common.collect.ImmutableList;
import com.googlecode.wicket.jquery.ui.form.dropdown.AjaxDropDownChoice;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.BootstrapFeedbackPanel;
import com.jeroensteenbeeke.hyperion.solstice.data.IByFunctionModel;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.hyperion.webcomponents.core.form.choice.LambdaRenderer;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarGroupDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.ScaledMapDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarGroup;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapLink;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.FogOfWarGroupFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.ScaledMapFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.components.AbstractMapPreview;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.AuthenticatedPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.FogOfWarPreviewRenderer;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.ViewMapPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.tabletop.MapViewPage;
import io.vavr.collection.Seq;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import javax.inject.Inject;

public class CreateLinkPage extends AuthenticatedPage {
	private static final long serialVersionUID = 321415672754877135L;

	private final Form<MapLink> form;

	private final IByFunctionModel<FogOfWarGroup> sourceModel;

	private final AjaxDropDownChoice<ScaledMap> mapSelect;

	@Inject
	private ScaledMapDAO mapDAO;

	@Inject
	private MapService mapService;

	private WebMarkupContainer preview;

	public CreateLinkPage(FogOfWarGroup source) {
		super("Create link");

		this.sourceModel = ModelMaker.wrap(source);

		add(new Link<>("back", sourceModel.map(FogOfWarGroup::getMap)) {

			private static final long serialVersionUID = 8195565526321875207L;

			@Override
			public void onClick() {
				setResponsePage(new ViewMapPage(getModelObject()));
			}
		});

		add(preview = new WebMarkupContainer("preview"));
		preview.setOutputMarkupId(true);

		TextField<String> sourceField = new TextField<>("source", sourceModel.map(group -> String.format("%s in %s",
				group.getName(), group.getMap().getName()
		)));
		sourceField.setEnabled(false);

		CheckBox bidirectionalCheckbox = new CheckBox("bidirectional", Model.of(true));

		ScaledMapFilter mapFilter = new ScaledMapFilter();
		mapFilter.owner(getUser());
		mapFilter.folder().orderBy(true);
		mapFilter.name().orderBy(true);

		DropDownChoice<FogOfWarGroup> targetSelect = new AjaxDropDownChoice<FogOfWarGroup>("target", Model.of(), ModelMaker.wrapList(ImmutableList.of(source)),
				LambdaRenderer.of(FogOfWarGroup::getName)) {

			private static final long serialVersionUID = 7704250964573457822L;

			@Override
			public void onSelectionChanged(AjaxRequestTarget target) {
				super.onSelectionChanged(target);

				FogOfWarGroup group = getModelObject();
				IModel<FogOfWarGroup> groupModel = getModel();

				WebMarkupContainer newPreview = new AbstractMapPreview("preview", group.getMap()) {
					private static final long serialVersionUID = -7642700691410263202L;

					@Override
					protected void addOnDomReadyJavaScript(String canvasId, StringBuilder js, double factor) {
						groupModel.getObject().getShapes().stream().map(s -> s.visit(new FogOfWarPreviewRenderer(canvasId, factor))).forEach(js::append);
					}
				};
				newPreview.setOutputMarkupId(true);
				preview.replaceWith(newPreview);

				target.add(preview, newPreview);

				preview = newPreview;
			}
		};
		targetSelect.setOutputMarkupId(true);
		targetSelect.setEnabled(false);
		targetSelect.setRequired(true);
		targetSelect.setNullValid(false);

		Seq<ScaledMap> mapOptions = mapDAO.findByFilter(mapFilter);
		mapSelect = new AjaxDropDownChoice<>("map", ModelMaker.wrap(ScaledMap.class),
			ModelMaker.wrapList(mapOptions.toJavaList()), LambdaRenderer.of(ScaledMap::getNameWithFolders)) {

			private static final long serialVersionUID = 7704250964573457822L;

			@Inject
			private FogOfWarGroupDAO groupDAO;

			@Override
			public void onSelectionChanged(AjaxRequestTarget target) {
				super.onSelectionChanged(target);

				ScaledMap selectedMap = getModelObject();

				WebMarkupContainer newPreview = new WebMarkupContainer("preview");
				newPreview.setOutputMarkupId(true);
				preview.replaceWith(newPreview);

				if (selectedMap == null) {
					targetSelect.setModel(Model.of());
					targetSelect.setChoices(ModelMaker.wrapList(ImmutableList.of(sourceModel.getObject())));
					targetSelect.setEnabled(false);
				} else {
					FogOfWarGroupFilter groupFilter = new FogOfWarGroupFilter();
					groupFilter.map(selectedMap);
					groupFilter.name().orderBy(true);

					final Seq<FogOfWarGroup> allOptions = groupDAO.findByFilter(groupFilter);
					Seq<FogOfWarGroup> actualOptions = allOptions;

					for (MapLink link : sourceModel.getObject().getLinks()) {
						actualOptions = actualOptions.remove(link.getTargetGroup());
					}

					targetSelect.setChoices(ModelMaker.wrapList(actualOptions.nonEmpty() ?
						actualOptions.toJavaList() :
						allOptions.toJavaList()));
					targetSelect.setModel(ModelMaker.wrap(FogOfWarGroup.class));
					targetSelect.setEnabled(actualOptions.nonEmpty());
				}

				target.add(targetSelect, preview, newPreview);

				preview = newPreview;
			}
		};
		mapSelect.setRequired(true);
		mapSelect.setNullValid(false);

		add(form = new Form<>("linkForm") {

			private static final long serialVersionUID = 7704250964573457822L;

			@Override
			protected void onSubmit() {
				FogOfWarGroup source = sourceModel.getObject();
				FogOfWarGroup target = targetSelect.getModelObject();

				if (target == null) {
					error("Invalid selection");
					return;
				}

				mapService.createLink(source, target);
				if (bidirectionalCheckbox.getModelObject()) {
					mapService.createLink(target, source);
				}

				setResponsePage(new ViewMapPage(source.getMap()));

			}
		});

		form.add(sourceField);
		form.add(mapSelect);
		form.add(targetSelect);
		form.add(bidirectionalCheckbox);

		add(new SubmitLink("submit", form));

	}

	@Override
	protected void onDetach() {
		super.onDetach();

		sourceModel.detach();
	}
}
