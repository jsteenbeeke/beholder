package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster;

import com.jeroensteenbeeke.hyperion.heinlein.web.components.AjaxIconLink;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.BootstrapPagingNavigator;
import com.jeroensteenbeeke.hyperion.icons.fontawesome.FontAwesome;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.SessionLogService;
import com.jeroensteenbeeke.topiroll.beholder.dao.SessionLogItemDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.SessionLogIndex;
import com.jeroensteenbeeke.topiroll.beholder.entities.SessionLogItem;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.SessionLogItemFilter;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;

import javax.inject.Inject;
import java.time.format.DateTimeFormatter;

public class SessionLogPage extends com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.AuthenticatedPage {
	private static final long serialVersionUID = 1L;

	@Inject
	private SessionLogItemDAO itemDAO;

	@Inject
	private SessionLogService sessionLogService;

	private IModel<SessionLogIndex> indexModel;

	public SessionLogPage(SessionLogIndex index) {
		super("Session Log for " + DateTimeFormatter.ISO_LOCAL_DATE.format(index.getDay()));

		indexModel = ModelMaker.wrap(index);

		WebMarkupContainer container = new WebMarkupContainer("itemcontainer");
		container.setOutputMarkupId(true);

		SessionLogItemFilter itemFilter = new SessionLogItemFilter();
		itemFilter.logIndex(index);
		itemFilter.user(getUser());
		itemFilter.completed().orderBy(true);
		itemFilter.eventTime().orderBy(true);

		DataView<SessionLogItem> itemView = new DataView<>("items", FilterDataProvider.of(itemFilter, itemDAO)) {

			private static final long serialVersionUID = -3290458059401703537L;

			@Override
			protected void populateItem(Item<SessionLogItem> item) {
				item.add(new Label("time", item
					.getModel()
					.map(SessionLogItem::getEventTime)).add(AttributeModifier.append("class", item
					.getModel()
					.filter(SessionLogItem::isCompleted)
					.map(i -> "session-log-completed")
					.orElse(""))));

				item.add(new Label("event", item
					.getModel()
					.map(SessionLogItem::getEventDescription)).add(AttributeModifier.append("class", item
					.getModel()
					.filter(SessionLogItem::isCompleted)
					.map(i -> "session-log-completed")
					.orElse(""))));

				item.add(new AjaxIconLink<>("complete", item.getModel(), FontAwesome.check) {
					private static final long serialVersionUID = -382254881027131133L;

					@Override
					protected void onConfigure() {
						super.onConfigure();
						setVisibilityAllowed(!getModelObject().isCompleted());
					}

					@Override
					public void onClick(AjaxRequestTarget target) {
						sessionLogService.setCompleted(item.getModelObject()).ifOk(() -> target.add(container));
					}
				});
				item.add(new AjaxIconLink<>("uncomplete", item.getModel(), FontAwesome.not_equal) {
					private static final long serialVersionUID = -382254881027131133L;

					@Override
					protected void onConfigure() {
						super.onConfigure();
						setVisibilityAllowed(getModelObject().isCompleted());
					}

					@Override
					public void onClick(AjaxRequestTarget target) {
						sessionLogService.setNotCompleted(item.getModelObject())
										 .ifOk(() -> target.add(container));
					}
				});
			}
		};
		itemView.setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance());
		add(container);
		container.add(itemView);

		add(new BootstrapPagingNavigator("nav", itemView));

	}

	@Override
	protected void onDetach() {
		super.onDetach();
		indexModel.detach();
	}
}
