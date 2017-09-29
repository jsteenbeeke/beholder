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

import java.awt.Dimension;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.inject.Inject;

import com.jeroensteenbeeke.topiroll.beholder.web.components.AbstractMapPreview;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.UrlUtils;
import org.apache.wicket.request.cycle.RequestCycle;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableAdapter;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableBehavior;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.hyperion.util.ImageUtil;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.TokenInstanceDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.TokenInstanceFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.components.ImageContainer;

public class MoveTokenController extends Panel {
	

	private static final long serialVersionUID = 1L;

	@Inject
	private TokenInstanceDAO tokenDAO;

	@Inject
	private MapService mapService;
	
	private SortedMap<Integer, Integer> calculatedWidths;

	private DataView<TokenInstance> tokenView;

	private AbstractMapPreview previewImage;

	private WebMarkupContainer precisionContainer;

	public MoveTokenController(String id, MapView view, ScaledMap map) {
		super(id);

		this.calculatedWidths = new TreeMap<>();

		TokenInstanceFilter filter = new TokenInstanceFilter();
		filter.map().set(map);
		filter.show().set(true);
		filter.badge().orderBy(true);

		tokenView = new DataView<TokenInstance>(
				"tokens", FilterDataProvider.of(filter, tokenDAO)) {
			private static final long serialVersionUID = 1L;

			@Inject
			private MapService mapService;

			@Override
			protected void populateItem(Item<TokenInstance> item) {
				TokenInstance instance = item.getModelObject();

				int squareSize = map.getSquareSize();

				int wh = squareSize
						* instance.getDefinition().getDiameterInSquares();

				calculatedWidths.put(item.getIndex(), wh + 4);

				ContextImage image = new ContextImage("token",
						String.format("tokens/%d?antiCache=%d",
								instance.getDefinition().getId(),
								System.currentTimeMillis()));
				image.add(AttributeModifier.replace("style",
						new LoadableDetachableModel<String>() {
							private static final long serialVersionUID = 1L;

							@Override
							protected String load() {
								int index = item.getIndex();
								TokenInstance i = item.getModelObject();
								int left = i.getOffsetX();
								int top = i.getOffsetY() - 1;

								for (int v : calculatedWidths.headMap(index)
										.values()) {
									left = left - v;
								}

								return String.format(
										"left: %dpx; top: %dpx; max-width: %dpx !important; " +
												"width: %dpx; height: %dpx; max-height: %dpx " +
												"!important;",
										left, top, wh, wh, wh, wh);
							}

						}));

				Options draggableOptions = new Options();
				draggableOptions.set("opacity", "0.5");
				draggableOptions.set("containment", Options.asString("parent"));
				image.add(new DraggableBehavior(draggableOptions,
						new DraggableAdapter() {
							private static final long serialVersionUID = 1L;

							@Override
							public boolean isStopEventEnabled() {

								return true;
							}

							@Override
							public void onDragStop(AjaxRequestTarget target,
									int top, int left) {
								super.onDragStop(target, top, left);

								int x = left;

								for (int v : calculatedWidths
										.headMap(item.getIndex()).values()) {
									x = x + v;
								}

								mapService.updateTokenLocation(
										item.getModelObject(), x, top + 1);
								
								target.add(precisionContainer);
							}
						}));

				item.add(image);

			}

		};
		
		

		previewImage = new AbstractMapPreview("preview",map) {
			@Override
			protected void addOnDomReadyJavaScript(String canvasId, StringBuilder js, double factor) {

			}
		};

		previewImage.add(tokenView);
		previewImage.setOutputMarkupId(true);

		add(previewImage);
		
		precisionContainer = new WebMarkupContainer("pcontainer");
		precisionContainer.setOutputMarkupId(true);
		add(precisionContainer);
		
		precisionContainer.add(new DataView<TokenInstance>("precision",
				FilterDataProvider.of(filter, tokenDAO)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<TokenInstance> item) {
				TokenInstance token = item.getModelObject();
				
				item.add(new Label("name", token.getLabel()));

				ScaledMap map = token.getMap();
				
				int squareSize = map.getSquareSize();
				
				int xmax = map.getBasicWidth() - token.getDefinition().getDiameterInSquares()*squareSize;
				int ymax = map.getBasicHeight() - token.getDefinition().getDiameterInSquares()*squareSize;
				
				NumberTextField<Integer> xlarge = new NumberTextField<Integer>(
						"xlarge", Model.of(token.getOffsetX()));
				xlarge.setStep(squareSize);
				xlarge.setMinimum(0);
				xlarge.setMaximum(xmax);
				
				NumberTextField<Integer> ylarge = new NumberTextField<Integer>(
						"ylarge", Model.of(token.getOffsetY()));
				ylarge.setStep(squareSize);
				ylarge.setMinimum(0);
				ylarge.setMaximum(ymax);
				
				NumberTextField<Integer> xsmall = new NumberTextField<Integer>(
						"xsmall", Model.of(token.getOffsetX()));
				xsmall.setMinimum(0);
				xsmall.setMaximum(xmax);
				
				NumberTextField<Integer> ysmall = new NumberTextField<Integer>(
						"ysmall", Model.of(token.getOffsetY()));
				ysmall.setMinimum(0);
				ysmall.setMaximum(ymax);
				
				
				
				item.add(xlarge);
				item.add(ylarge);
				
				xlarge.add(new TokenCoordinateUpdateBehavior(xsmall, item.getModel(), xlarge, ylarge));
				ylarge.add(new TokenCoordinateUpdateBehavior(ysmall, item.getModel(), xlarge, ylarge));
				
				item.add(xsmall);
				item.add(ysmall);
				
				xsmall.add(new TokenCoordinateUpdateBehavior(xlarge, item.getModel(), xsmall, ysmall));
				ysmall.add(new TokenCoordinateUpdateBehavior(ylarge, item.getModel(), xsmall, ysmall));
				
				
				xsmall.setOutputMarkupId(true);
				ysmall.setOutputMarkupId(true);
				xlarge.setOutputMarkupId(true);
				ylarge.setOutputMarkupId(true);
			
			}

		});

	}
	
	public class TokenCoordinateUpdateBehavior extends AjaxFormComponentUpdatingBehavior {

		private static final long serialVersionUID = 1L;
		
		private NumberTextField<Integer> targetField;
		
		private IModel<TokenInstance> instanceModel;
		
		private NumberTextField<Integer> xfield;

		private NumberTextField<Integer> yfield;
		
		public TokenCoordinateUpdateBehavior(NumberTextField<Integer> targetField, IModel<TokenInstance> instanceModel, NumberTextField<Integer> xfield, NumberTextField<Integer> yfield) {
			super("change");
			this.targetField = targetField;
			this.instanceModel = instanceModel;
			this.xfield = xfield;
			this.yfield = yfield;
		}
		
		@Override
		protected void onUpdate(AjaxRequestTarget target) {
			@SuppressWarnings("unchecked")
			NumberTextField<Integer> source = (NumberTextField<Integer>) getComponent();
			targetField.setModelObject(source.getModelObject());
			target.add(targetField);
			
			int x = xfield.getModelObject();
			int y = yfield.getModelObject();
			
			mapService.updateTokenLocation(instanceModel.getObject(), x, y);
			
			target.add(previewImage);
			
		}
	}
}
