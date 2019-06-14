package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster;

import com.jeroensteenbeeke.hyperion.heinlein.web.components.BootstrapPagingNavigator;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.IconLink;
import com.jeroensteenbeeke.hyperion.icons.fontawesome.FontAwesome;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.topiroll.beholder.dao.SessionLogIndexDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.SessionLogIndex;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.SessionLogIndexFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;

import javax.inject.Inject;

public class SessionLogOverviewPage extends AuthenticatedPage {



	private static final long serialVersionUID = 1L;

	@Inject
	private SessionLogIndexDAO indexDAO;

	public  SessionLogOverviewPage() {
		super("Session Log Overview");

		SessionLogIndexFilter filter = new SessionLogIndexFilter();
		filter.owner(getUser());
		filter.day().orderBy(false);

		DataView<SessionLogIndex> indexView = new DataView<>("indices", FilterDataProvider.of(filter, indexDAO)) {

			private static final long serialVersionUID = 3965261355964888504L;

			@Override
			protected void populateItem(Item<SessionLogIndex> item) {
				item.add(new Label("day", item.getModel().map(SessionLogIndex::getDay)));
				item.add(new IconLink<>("view", item.getModel(), FontAwesome.eye) {
					private static final long serialVersionUID = -4386093805155458459L;

					@Override
					public void onClick() {
						setResponsePage(new SessionLogPage(getModelObject()));
					}
				});
			}
		};
		add(indexView);

		add(new BootstrapPagingNavigator("nav", indexView));

	}

}
