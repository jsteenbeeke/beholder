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

package com.jeroensteenbeeke.topiroll.beholder.web.components.mapcontrol;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.danekja.java.util.function.serializable.SerializableBiConsumer;

import com.jeroensteenbeeke.hyperion.heinlein.web.components.AjaxIconLink;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.GlyphIcon;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.beans.MarkerService;
import com.jeroensteenbeeke.topiroll.beholder.dao.TokenInstanceDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenBorderType;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.TokenInstanceFilter;

public abstract class TokenStateController extends Panel {

	public class MarkerLink extends AjaxLink<TokenInstance> {
		private static final long serialVersionUID = 1L;

		private final SerializableBiConsumer<MapView, TokenInstance> onClick;

		private IModel<MapView> viewModel;

		private IModel<TokenInstance> tokenModel;

		public MarkerLink(String id,
				SerializableBiConsumer<MapView, TokenInstance> onClick,
				MapView view, TokenInstance token) {
			super(id);
			this.onClick = onClick;
			this.viewModel = ModelMaker.wrap(view);
			this.tokenModel = ModelMaker.wrap(token);
		}

		@Override
		protected void onDetach() {
			super.onDetach();
			viewModel.detach();
			tokenModel.detach();
		}

		@Override
		public void onClick(AjaxRequestTarget target) {
			onClick.accept(viewModel.getObject(), tokenModel.getObject());
			onMarkerCreated(target);

		}

	}

	private static final String ID_MAX_HP = "maxHP";

	private static final String ID_CURRENT_HP = "currentHP";

	private static final long serialVersionUID = 1L;

	@Inject
	private TokenInstanceDAO tokenDAO;

	@Inject
	private MapService mapService;
	
	@Inject
	private MarkerService markerService;

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
				.collect(Collectors.toCollection(ArrayList::new));

		ListView<TokenInstance> tokenView = new ListView<TokenInstance>(
				"tokens", ModelMaker.wrap(tokens)) {
			private static final long serialVersionUID = 1L;

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

						link.add(AttributeModifier.replace("class",
								new LoadableDetachableModel<String>() {
									private static final long serialVersionUID = 1L;

									@Override
									protected String load() {
										if (type == item.getModelObject()
												.getBorderType()) {
											return "btn btn-primary";
										}

										return "btn btn-default";
									}
								}));

						link.setBody(Model.of(type.name()));
						_item.add(link);
					}
				});

				NumberTextField<Integer> currentHitpointsField = new NumberTextField<>(
						ID_CURRENT_HP, Model.of(instance.getCurrentHitpoints()),
						Integer.class);
				NumberTextField<Integer> maxHitpointsField = new NumberTextField<>(
						ID_MAX_HP, Model.of(instance.getMaxHitpoints()),
						Integer.class);
				TextField<String> noteField = new TextField<>("note",
						Model.of(instance.getNote()));

				currentHitpointsField
						.add(new AjaxFormComponentUpdatingBehavior("blur") {
							private static final long serialVersionUID = 1L;

							@Override
							protected void onUpdate(AjaxRequestTarget target) {
								mapService.setTokenHP(item.getModelObject(),
										currentHitpointsField.getModelObject(),
										maxHitpointsField.getModelObject());

							}
						});

				maxHitpointsField
						.add(new AjaxFormComponentUpdatingBehavior("blur") {
							private static final long serialVersionUID = 1L;

							@Override
							protected void onUpdate(AjaxRequestTarget target) {
								mapService.setTokenHP(item.getModelObject(),
										currentHitpointsField.getModelObject(),
										maxHitpointsField.getModelObject());

							}
						});
				noteField.add(new AjaxFormComponentUpdatingBehavior("blur") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						mapService.setTokenNote(item.getModelObject(),
								noteField.getModelObject());

					}
				});

				item.add(currentHitpointsField);
				item.add(maxHitpointsField);
				item.add(noteField);

				item.add(new AjaxIconLink<TokenInstance>("hide",
						item.getModel(), GlyphIcon.eyeClose) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						mapService.hideToken(getModelObject());

						replaceMe(target);
					}

				});

				item.add(new MarkerLink("circle", markerService::createCircle,
						viewModel.getObject(), item.getModelObject()));
				item.add(new MarkerLink("cone", markerService::createCone,
						viewModel.getObject(), item.getModelObject()));
				item.add(new MarkerLink("cube", markerService::createCube,
						viewModel.getObject(), item.getModelObject()));
				item.add(new MarkerLink("line", markerService::createLine,
						viewModel.getObject(), item.getModelObject()));
			}
		};

		add(tokenView);

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

	public abstract void onMarkerCreated(AjaxRequestTarget target);
}
