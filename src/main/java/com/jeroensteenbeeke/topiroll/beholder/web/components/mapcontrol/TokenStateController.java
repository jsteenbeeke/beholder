package com.jeroensteenbeeke.topiroll.beholder.web.components.mapcontrol;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.TokenInstanceDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenBorderIntensity;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenBorderType;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.TokenInstanceFilter;

public class TokenStateController extends Panel {
	private static final long serialVersionUID = 1L;

	@Inject
	private TokenInstanceDAO tokenDAO;

	public TokenStateController(String id, MapView view, ScaledMap map) {
		super(id);
		setOutputMarkupId(true);

		TokenInstanceFilter filter = new TokenInstanceFilter();
		if (map != null) {
			filter.map().set(map);
		} else {
			// Map can't be null, so should return empty set
			filter.map().isNull();
		}
		filter.badge().orderBy(true);

		List<TokenInstance> tokens = tokenDAO.findByFilter(filter).stream()
				.filter(i -> i.isVisible(view, true))
				.collect(Collectors.toList());

		add(new ListView<TokenInstance>("tokens", ModelMaker.wrap(tokens)) {
			private static final long serialVersionUID = 1L;

			@Inject
			private MapService mapService;

			@Override
			protected void populateItem(ListItem<TokenInstance> item) {
				TokenInstance instance = item.getModelObject();

				item.add(new Label("token", instance.getLabel()));
				item.add(new ListView<TokenBorderType>("types",
						ModelMaker.forEnum(TokenBorderType.class)) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void populateItem(
							ListItem<TokenBorderType> _item) {
						TokenBorderType type = _item.getModelObject();

						AjaxLink<TokenBorderType> link = new AjaxLink<TokenBorderType>(
								"button") {
							private static final long serialVersionUID = 1L;

							@Override
							public void onClick(AjaxRequestTarget target) {
								mapService.setTokenBorderType(
										item.getModelObject(), type);

								target.add(TokenStateController.this);

							}

						};
						
						link.add(AttributeModifier.replace("class", new LoadableDetachableModel<String>() {
							private static final long serialVersionUID = 1L;

							@Override
							protected String load() {
								if (type == item.getModelObject().getBorderType()) {
									return "btn btn-primary";
								}
								
								return "btn btn-default";
							}
						}));

						link.setBody(Model.of(type.name()));
						_item.add(link);
					}
				});
				item.add(new ListView<TokenBorderIntensity>("health",
						ModelMaker.forEnum(TokenBorderIntensity.class)) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void populateItem(
							ListItem<TokenBorderIntensity> _item) {
						TokenBorderIntensity intensity = _item.getModelObject();

						AjaxLink<TokenBorderType> link = new AjaxLink<TokenBorderType>(
								"button") {
							private static final long serialVersionUID = 1L;

							@Override
							public void onClick(AjaxRequestTarget target) {
								mapService.setTokenBorderIntensity(
										item.getModelObject(), intensity);

								target.add(TokenStateController.this);

							}

						};
						
						link.add(AttributeModifier.replace("class", new LoadableDetachableModel<String>() {
							private static final long serialVersionUID = 1L;

							@Override
							protected String load() {
								if (intensity == item.getModelObject().getBorderIntensity()) {
									return "btn btn-primary";
								}
								
								return "btn btn-default";
							}
						}));

						link.setBody(Model.of(intensity.getDescription()));
						_item.add(link);
					}
				});

			}
		});
	}
}
