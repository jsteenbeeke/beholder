package com.jeroensteenbeeke.topiroll.beholder.web.components.mapcontrol;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

import com.jeroensteenbeeke.hyperion.heinlein.web.components.AjaxIconLink;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.GlyphIcon;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.TokenInstanceDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenBorderType;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.TokenInstanceFilter;

public abstract class TokenStateController extends Panel {
	private static final long serialVersionUID = 1L;

	@Inject
	private TokenInstanceDAO tokenDAO;
	
	private IModel<MapView> viewModel;
	
	private IModel<ScaledMap> mapModel;

	public TokenStateController(String id, MapView view, ScaledMap map) {
		super(id);
		setOutputMarkupId(true);
		
		this.viewModel = ModelMaker.wrap(view);
		this.mapModel = ModelMaker.wrap(map);

		TokenInstanceFilter filter = new TokenInstanceFilter();
		if (map != null) {
			filter.map().set(map);
		} else {
			// Map can't be null, so should return empty set
			filter.map().isNull();
		}
		filter.show().set(true);
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
								
								replaceMe(target);

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
				
				NumberTextField<Integer> currentHitpointsField = new NumberTextField<>("currentHP", Model.of(instance.getCurrentHitpoints()), Integer.class);
				NumberTextField<Integer> maxHitpointsField = new NumberTextField<>("maxHP", Model.of(instance.getMaxHitpoints()), Integer.class);
				
				Form<TokenInstance> healthForm = new Form<TokenInstance>("health") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onSubmit() {
						mapService.setTokenHP(item.getModelObject(), currentHitpointsField.getModelObject(), maxHitpointsField.getModelObject());
}					
					
				};
				
				healthForm.add(currentHitpointsField);
				healthForm.add(maxHitpointsField);
				
				healthForm.add(new AjaxSubmitLink("update", healthForm) {
					private static final long serialVersionUID = 1L;
					
					@Override
					protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
						replaceMe(target);
					}
				});
				item.add(healthForm);
				
				item.add(new AjaxIconLink<TokenInstance>("hide", item.getModel(), GlyphIcon.eyeClose) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						mapService.hideToken(getModelObject());

						replaceMe(target);
					}

					
				});

			}
		});
		
		
	}
	
	public ScaledMap getMap() {
		return mapModel.getObject();
	}
	
	@Override
	protected void onDetach() {
		super.onDetach();
		viewModel.detach();
		mapModel.detach();
	}
	
	public abstract void replaceMe(AjaxRequestTarget target);
}
