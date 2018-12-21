package com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.exploration;

import com.google.common.collect.Lists;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.AjaxIconLink;
import com.jeroensteenbeeke.hyperion.icons.fontawesome.FontAwesome;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.BeholderRegistry;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.PortraitDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.PortraitVisibilityDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.YouTubePlaylistDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.Portrait;
import com.jeroensteenbeeke.topiroll.beholder.entities.PortraitVisibilityLocation;
import com.jeroensteenbeeke.topiroll.beholder.entities.YouTubePlaylist;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.PortraitFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.PortraitVisibilityFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.YouTubePlaylistFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewCallback;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewPanel;
import com.jeroensteenbeeke.topiroll.beholder.web.data.JSPlaylist;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ExternalImage;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

import javax.inject.Inject;
import java.util.Random;

public class PortraitsWindow extends DMViewPanel<MapView> {
	private static final long serialVersionUID = -2255734962780199594L;
	@Inject
	private PortraitDAO portraitDAO;

	@Inject
	private MapService mapService;

	public PortraitsWindow(String id, MapView view, DMViewCallback callback) {
		super(id, ModelMaker.wrap(view));

		PortraitFilter portraitFilter = new PortraitFilter();
		portraitFilter.owner(view.getOwner()).name().orderBy(true);

		WebMarkupContainer container = new WebMarkupContainer("container");
		container.setOutputMarkupId(true);

		DataView<Portrait> portraitView = new DataView<Portrait>("portraits",
				FilterDataProvider.of(portraitFilter, portraitDAO)) {
			@Override
			protected void populateItem(Item<Portrait> item) {
				Portrait portrait = item.getModelObject();
				item.add(new Label("name", portrait.getName()));
				item.add(new ExternalImage("thumb",
						portrait.getImageUrl()));
				item.add(new ListView<PortraitVisibilityLocation>("locations",
						Lists.newArrayList(PortraitVisibilityLocation.values())) {

					private static final long serialVersionUID = 152264260845309393L;
					@Inject
					private PortraitVisibilityDAO visibilityDAO;

					@Override
					protected void populateItem(ListItem<PortraitVisibilityLocation> innerItem) {
						PortraitVisibilityLocation location = innerItem.getModelObject();

						final boolean selected = visibilityDAO.findByFilter(
								new PortraitVisibilityFilter().view(PortraitsWindow.this.getModelObject())
										.portrait(item.getModelObject()))
								.find(v -> v.getLocation().equals(location)).isDefined();

						AjaxLink<PortraitVisibilityLocation> link = new AjaxLink<PortraitVisibilityLocation>
								("button") {

							private static final long serialVersionUID = -7633978000647082838L;

							@Override
							public void onClick(AjaxRequestTarget target) {
								if (selected) {
									mapService.unselectPortrait(PortraitsWindow.this.getModelObject(), item.getModelObject(),
											location);
								} else {
									mapService.selectPortrait(PortraitsWindow.this.getModelObject(), item.getModelObject(),
											location);
								}

								target.add(container);
							}
						};


						link.add(AttributeModifier.replace("class", new LoadableDetachableModel<String>() {
							private static final long serialVersionUID = -317551053297479895L;

							@Override
							protected String load() {
								if (!selected) {
									return "btn btn-default";
								}
								return "btn btn-primary";
							}
						}));

						link.setBody(Model.of(location.getDisplayValue()));

						innerItem.add(link);
					}
				});
			}
		};
		portraitView.setOutputMarkupId(true);
		container.add(portraitView);
		add(container);
	}
}
