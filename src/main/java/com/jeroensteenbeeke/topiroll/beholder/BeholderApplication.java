/**
 * This file is part of Beholder
 * (C) 2016 Jeroen Steenbeeke
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jeroensteenbeeke.topiroll.beholder;

import com.jeroensteenbeeke.topiroll.beholder.beans.RollBarData;
import com.jeroensteenbeeke.topiroll.beholder.beans.impl.RollbarClientListener;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.ws.api.registry.IWebSocketConnectionRegistry;
import org.apache.wicket.protocol.ws.api.registry.SimpleWebSocketConnectionRegistry;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.jeroensteenbeeke.hyperion.heinlein.web.Heinlein;
import com.jeroensteenbeeke.hyperion.meld.web.EntityEncapsulator;
import com.jeroensteenbeeke.hyperion.social.Slack;
import com.jeroensteenbeeke.hyperion.solstice.data.factory.SolsticeEntityEncapsulatorFactory;
import com.jeroensteenbeeke.hyperion.solstice.spring.ApplicationContextProvider;
import com.jeroensteenbeeke.hyperion.tardis.scheduler.wicket.HyperionScheduler;
import com.jeroensteenbeeke.topiroll.beholder.beans.URLService;
import com.jeroensteenbeeke.topiroll.beholder.web.BeholderSession;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.HomePage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.MapViewPage;
import com.jeroensteenbeeke.topiroll.beholder.web.resources.MapResource;
import com.jeroensteenbeeke.topiroll.beholder.web.resources.TokenResource;

public class BeholderApplication extends WebApplication
		implements ApplicationContextProvider {
	private ApplicationContext ctx;

	private IWebSocketConnectionRegistry webSocketRegistry;

	@Override
	protected void init() {
		super.init();

		getComponentInstantiationListeners()
				.add(new SpringComponentInjector(this));
		ctx = WebApplicationContextUtils
				.getWebApplicationContext(getServletContext());

		String sourceURL = ctx.getBean(URLService.class).getSourceURL();

		if (sourceURL == null || sourceURL.isEmpty()) {
			throw new IllegalStateException(
					"This software is licensed under the Affero GPL, which requires you to provide source code to all " +
							"users. Please input the source URL");
		}

		EntityEncapsulator.setFactory(new SolsticeEntityEncapsulatorFactory());

		HyperionScheduler.getScheduler().setApplication(this);

		getMarkupSettings().setStripWicketTags(true);

		Heinlein.init(this, "css/beholder-web.css");

		Slack.integration.initialize(this, "");

		mountPage("views/${identifier}", MapViewPage.class);
		mountResource("maps/${mapId}",
				new ResourceReference(BeholderApplication.class, "maps") {
					private static final long serialVersionUID = 1L;

					@Override
					public IResource getResource() {

						return new MapResource();
					}
				});
		mountResource("tokens/${tokenId}",
				new ResourceReference(BeholderApplication.class, "tokens") {
					private static final long serialVersionUID = 1L;

					@Override
					public IResource getResource() {

						return new TokenResource();
					}
				});

		webSocketRegistry = new SimpleWebSocketConnectionRegistry();

		RollBarData data = ctx.getBean(RollBarData.class);

		if (data != null && data.getClientKey() != null) {
			BeholderApplication.get().getHeaderContributorListeners()
					.add(new RollbarClientListener(data.getClientKey(), data.getEnvironment()));
		}

	}

	public IWebSocketConnectionRegistry getWebSocketRegistry() {
		return webSocketRegistry;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		HyperionScheduler.getScheduler().shutdown();
	}

	@Override
	public Class<? extends Page> getHomePage() {
		return HomePage.class;
	}

	public static BeholderApplication get() {
		return (BeholderApplication) WebApplication.get();
	}

	@Override
	public ApplicationContext getApplicationContext() {
		return ctx;
	}

	@Override
	public Session newSession(Request request, Response response) {

		return new BeholderSession(request);
	}

	public <T> T getBean(Class<T> beanClass) {
		return getApplicationContext().getBean(beanClass);
	}
}
