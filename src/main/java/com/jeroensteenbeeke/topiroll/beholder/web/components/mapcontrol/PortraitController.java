package com.jeroensteenbeeke.topiroll.beholder.web.components.mapcontrol;

import com.google.common.collect.Lists;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.hyperion.webcomponents.core.TypedPanel;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.PortraitDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.PortraitVisibilityDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.Portrait;
import com.jeroensteenbeeke.topiroll.beholder.entities.PortraitVisibilityLocation;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.PortraitFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.PortraitVisibilityFilter;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ExternalImage;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public abstract class PortraitController extends TypedPanel<MapView> {
	@Inject
	private MapService mapService;

	@Inject
	private PortraitDAO portraitDAO;

	private IModel<MapView> viewModel;

	public PortraitController(String id,
							  @Nonnull MapView view) {
		super(id);
		setOutputMarkupId(true);
		this.viewModel = ModelMaker.wrap(view);

		PortraitFilter portraitFilter = new PortraitFilter();
		portraitFilter.owner(view.getOwner()).name().orderBy(true);

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

					@Inject
					private PortraitVisibilityDAO visibilityDAO;

					@Override
					protected void populateItem(ListItem<PortraitVisibilityLocation> innerItem) {
						PortraitVisibilityLocation location = innerItem.getModelObject();

						final boolean selected = visibilityDAO.findByFilter(
								new PortraitVisibilityFilter().view(viewModel.getObject())
										.portrait(item.getModelObject()))
								.find(v -> v.getLocation().equals(location)).isDefined();

						AjaxLink<PortraitVisibilityLocation> link = new AjaxLink<PortraitVisibilityLocation>
								("button") {

							@Override
							public void onClick(AjaxRequestTarget target) {
								if (selected) {
									mapService.unselectPortrait(viewModel.getObject(), item.getModelObject(),
											location);
								} else {
									mapService.selectPortrait(viewModel.getObject(), item.getModelObject(),
											location);
								}

								replaceMe(target);
							}
						};


						link.add(AttributeModifier.replace("class", new LoadableDetachableModel<String>() {
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
		add(portraitView);
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		viewModel.detach();
	}

	protected abstract void replaceMe(AjaxRequestTarget target);
}
