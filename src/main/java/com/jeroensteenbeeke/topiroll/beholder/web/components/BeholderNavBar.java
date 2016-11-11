package com.jeroensteenbeeke.topiroll.beholder.web.components;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.time.Duration;

import com.jeroensteenbeeke.topiroll.beholder.beans.MenuItemProvider;
import com.jeroensteenbeeke.topiroll.beholder.beans.MenuService;
import com.jeroensteenbeeke.topiroll.beholder.web.BeholderSession;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.HomePage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.OverviewPage;

public class BeholderNavBar extends org.apache.wicket.markup.html.panel.Panel {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private MenuService menuService;

	public BeholderNavBar(@Nonnull String id) {
		super(id);
		
		Link<MenuItemProvider> brandLink = new Link<MenuItemProvider>("brand") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				
				setResponsePage(new OverviewPage());
			}
			
		};
		
		brandLink.add(new UserImage("image"));
		
		add(brandLink);
		
		add(new ListView<MenuItemProvider>("items", menuService.getProviders()) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<MenuItemProvider> item) {
				MenuItemProvider navItem = item.getModelObject();
				
				Link<MenuItemProvider> link = new Link<MenuItemProvider>("link") {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						setResponsePage(navItem.onClick());
					}
					
				};
				
				if (navItem.isSelected(getPage())) {
					item.add(AttributeModifier.replace("class", "active"));
				}
				
				
				item.add(link);
				
				link.add(new Label("label", new LoadableDetachableModel<String>() {

					private static final long serialVersionUID = 1L;

					@Override
					protected String load() {
						return navItem.getLabel();
					}
				}));
				link.add(new Label("badge", new LoadableDetachableModel<Serializable>() {
					private static final long serialVersionUID = 1L;

					@Override
					protected Serializable load() {
						return navItem.getBadge();
					}
				}).setOutputMarkupId(true).setVisible(navItem.isBadgeSupported()).add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(10))));
				
				
			}
		});

		add(new Link<Void>("logout") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				BeholderSession.get().invalidate();
				setResponsePage(HomePage.class);
			}

		});
	}

	
}
