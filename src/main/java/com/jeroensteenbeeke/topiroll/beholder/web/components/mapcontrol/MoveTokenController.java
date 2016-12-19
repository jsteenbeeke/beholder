package com.jeroensteenbeeke.topiroll.beholder.web.components.mapcontrol;

import java.awt.Dimension;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.inject.Inject;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.UrlUtils;
import org.apache.wicket.request.cycle.RequestCycle;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableAdapter;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableBehavior;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
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

	private IModel<ScaledMap> mapModel;
	
	private SortedMap<Integer,Integer> calculatedWidths;

	public MoveTokenController(String id, MapView view, ScaledMap map) {
		super(id);

		this.mapModel = ModelMaker.wrap(map);
		this.calculatedWidths = new TreeMap<>();
		
		Dimension dimensions = ImageUtil.getImageDimensions(map.getData());

		TokenInstanceFilter filter = new TokenInstanceFilter();
		filter.map().set(map);
		filter.show().set(true);

		DataView<TokenInstance> tokenView = new DataView<TokenInstance>(
				"tokens", FilterDataProvider.of(filter, tokenDAO)) {
			private static final long serialVersionUID = 1L;

			@Inject
			private MapService mapService;

			@Override
			protected void populateItem(Item<TokenInstance> item) {
				TokenInstance instance = item.getModelObject();
				
				int squareSize = map != null ? map.getSquareSize() : 10;

				int wh = (int) (squareSize * instance.getDefinition().getDiameterInSquares());
				
				calculatedWidths.put(item.getIndex(), wh);
				

				ContextImage image = new ContextImage("token", String.format(
						"tokens/%d?antiCache=%d", instance.getDefinition().getId(), System.currentTimeMillis()));
				image.add(AttributeModifier.replace("style", new LoadableDetachableModel<String>() {
					private static final long serialVersionUID = 1L;

					@Override
					protected String load() {
						int index = item.getIndex();
						TokenInstance i = item.getModelObject();
						int left = i.getOffsetX();
						int top = i.getOffsetY();
						
						for (int v: calculatedWidths.headMap(index).values()) {
							left = left - v;
						}
						
						return String.format("left: %dpx; top: %dpx; width: %dpx; height: %dpx;",
								left, top, wh, wh);
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
								
								int x = left + wh;
								
								for (int v: calculatedWidths.headMap(item.getIndex()).values()) {
									x = x + v;
								}
								
								
								mapService.updateTokenLocation(
										item.getModelObject(), x, top);
							}
						}));

				item.add(image);

			}

		};

		final ImageContainer previewImage = new ImageContainer("preview",
				UrlUtils.rewriteToContextRelative(String.format("maps/%d?antiCache=%d", map.getId(), System.currentTimeMillis()), RequestCycle.get()), dimensions);

		previewImage.add(tokenView);

		add(previewImage);
	}
}
