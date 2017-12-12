package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.combat;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableAdapter;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableBehavior;
import com.jeroensteenbeeke.hyperion.data.DomainObject;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.BootstrapBasePage;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.entities.AreaMarker;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;
import com.jeroensteenbeeke.topiroll.beholder.web.BeholderSession;
import com.jeroensteenbeeke.topiroll.beholder.web.components.AbstractMapPreview;
import com.jeroensteenbeeke.topiroll.beholder.web.components.OnClickBehavior;
import com.jeroensteenbeeke.topiroll.beholder.web.components.combat.CombatModeCallback;
import com.jeroensteenbeeke.topiroll.beholder.web.components.combat.CombatModePanel;
import com.jeroensteenbeeke.topiroll.beholder.web.components.combat.InitiativePanel;
import com.jeroensteenbeeke.topiroll.beholder.web.components.combat.TokenStatusPanel;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.HomePage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.ControlViewPage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BiFunction;

public class CombatControllerPage extends BootstrapBasePage implements CombatModeCallback {
	private static final String MODAL_ID = "modal";
	private final SortedMap<Integer, Integer> calculatedWidths;
	private final TokenStatusPanel
			tokenStatusPanel;

	private WebMarkupContainer modal;

	private IModel<TokenInstance> selectedToken = Model.of();

	private IModel<AreaMarker> selectedMarker = Model.of();

	@Inject
	private MapService mapService;
	private final AbstractMapPreview preview;

	public CombatControllerPage(MapView view) {
		super("Combat Mode");

		if (BeholderSession.get().getUser() == null) {
			BeholderSession.get().invalidate();
			throw new RestartResponseAtInterceptPageException(HomePage.class);
		}

		ScaledMap map = view.getSelectedMap();

		if (map == null) {
			throw new RestartResponseAtInterceptPageException(ControlViewPage.class);
		}

		int desiredWidth = (int) (map.getDisplayFactor(view) * map.getBasicWidth());

		preview = new AbstractMapPreview("preview", map, desiredWidth) {
			@Override
			protected void addOnDomReadyJavaScript(String canvasId, StringBuilder js,
												   double factor) {

			}

		};

		preview.add(new InitiativePanel("initiative", view));
		tokenStatusPanel = new TokenStatusPanel("tokenStatus", this);
		preview.add(tokenStatusPanel.setVisible(false));

		this.calculatedWidths = new TreeMap<>();
		preview.add(new ListView<TokenInstance>("tokens", ModelMaker.wrapList(map.getTokens(),
				false)) {
			@Override
			protected void populateItem(ListItem<TokenInstance> item) {
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

								left = preview.translateToScaledImageSize(left);
								top = preview.translateToScaledImageSize(top);

								int actualWH = preview.translateToScaledImageSize(wh);

								String tokenBorderColor = instance.getBorderType().toHexColor();

								return String.format(
										"left: %dpx; top: %dpx; max-width: %dpx !important; " +
												"width: %dpx; height: %dpx; max-height: %dpx " +
												"!important; border-radius: 100%%; border: 1px " +
												"solid" +
												" " +
												"#%s",
										left, top, actualWH, actualWH, actualWH, actualWH,
										tokenBorderColor);
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

								x = preview.translateToRealImageSize(x);
								int y = preview.translateToRealImageSize(top + 1);

								mapService.updateTokenLocation(
										item.getModelObject(), x, y);

								target.add(preview);
							}
						}));

				image.add(new OnClickBehavior() {
					@Override
					protected void onClick(AjaxRequestTarget target, ClickEvent event) {
						super.onClick(target, event);

						selectedToken = ModelMaker.wrap(item.getModelObject());

						tokenStatusPanel.setVisible(true);
						target.add(tokenStatusPanel);
					}
				});

				item.add(image);

			}
		});

		preview.add(new Link<MapView>("back", ModelMaker.wrap(view)) {

			@Override
			public void onClick() {
				setResponsePage(new ControlViewPage(getModelObject()));
			}
		});

		add(preview);

		add(modal = new WebMarkupContainer(MODAL_ID));
	}

	@Override
	public void redrawTokens(AjaxRequestTarget target) {
		target.add(preview);
	}

	@Override
	public TokenInstance getSelectedToken() {
		return selectedToken.getObject();
	}

	@Override
	public <T extends DomainObject> void createModalWindow(
			@Nonnull
					AjaxRequestTarget target,
			@Nonnull
					PanelConstructor<T> constructor,
			@Nonnull
					T object) {
		WebMarkupContainer oldModal = modal;
		oldModal.replaceWith(modal = constructor.apply(MODAL_ID, object, this));
		target.add(modal);
	}


}
