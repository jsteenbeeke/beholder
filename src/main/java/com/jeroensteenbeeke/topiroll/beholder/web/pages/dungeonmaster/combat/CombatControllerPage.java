package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.combat;

import com.jeroensteenbeeke.hyperion.heinlein.web.pages.BootstrapBasePage;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.InitiativeParticipantFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.BeholderSession;
import com.jeroensteenbeeke.topiroll.beholder.web.components.AbstractMapPreview;
import com.jeroensteenbeeke.topiroll.beholder.web.components.combat.InitiativePanel;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.HomePage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.ControlViewPage;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.markup.html.link.Link;

public class CombatControllerPage extends BootstrapBasePage {
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

		AbstractMapPreview preview = new AbstractMapPreview("preview", map, desiredWidth) {
			@Override
			protected void addOnDomReadyJavaScript(String canvasId, StringBuilder js, double factor) {

			}
		};

		preview.add(new InitiativePanel("initiative", view));

		preview.add(new Link<MapView>("back", ModelMaker.wrap(view)) {

			@Override
			public void onClick() {
				setResponsePage(new ControlViewPage(getModelObject()));
			}
		});

		add(preview);
	}
}
