/**
 * This file is part of Beholder
 * (C) 2016-2019 Jeroen Steenbeeke
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

import com.jeroensteenbeeke.hyperion.HyperionApp;
import com.jeroensteenbeeke.hyperion.heinlein.web.Heinlein;
import com.jeroensteenbeeke.hyperion.icons.fontawesome.FontAwesomeInitializer;
import com.jeroensteenbeeke.hyperion.meld.web.EntityEncapsulator;
import com.jeroensteenbeeke.hyperion.rollbar.RollBarReference;
import com.jeroensteenbeeke.hyperion.social.Slack;
import com.jeroensteenbeeke.hyperion.solstice.data.factory.SolsticeEntityEncapsulatorFactory;
import com.jeroensteenbeeke.hyperion.solstice.spring.ApplicationContextProvider;
import com.jeroensteenbeeke.hyperion.solstice.spring.ApplicationMetadataStore;
import com.jeroensteenbeeke.hyperion.tardis.scheduler.wicket.HyperionScheduler;
import com.jeroensteenbeeke.hyperion.util.ResourceUtil;
import com.jeroensteenbeeke.topiroll.beholder.beans.RollBarData;
import com.jeroensteenbeeke.topiroll.beholder.beans.URLService;
import com.jeroensteenbeeke.topiroll.beholder.beans.DeployNotificationContext;
import com.jeroensteenbeeke.topiroll.beholder.jobs.InitializeCompendiumJob;
import com.jeroensteenbeeke.topiroll.beholder.web.BeholderSession;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.HomePage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.InternalErrorPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.PageExpiredPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.tabletop.MapViewPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.tabletop.MusicPage;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.ws.api.registry.IWebSocketConnectionRegistry;
import org.apache.wicket.protocol.ws.api.registry.SimpleWebSocketConnectionRegistry;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.cycle.IRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.caching.FilenameWithVersionResourceCachingStrategy;
import org.apache.wicket.request.resource.caching.version.StaticResourceVersion;
import org.apache.wicket.resource.JQueryResourceReference;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BeholderApplication extends WebApplication
	implements ApplicationContextProvider {
	static final String KEY_BEHOLDER_CURRENT_VERSION = "beholder.current.version";

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

		if (sourceURL.isEmpty()) {
			throw new IllegalStateException(
				"This software is licensed under the Affero GPL, which requires you to provide source code to all "
					+ "users. Please input the source URL");
		}

		getResourceSettings().setCachingStrategy(
			new FilenameWithVersionResourceCachingStrategy(
				new StaticResourceVersion(DateTimeFormatter.ISO_DATE_TIME
					.format(LocalDateTime.now()))));

		EntityEncapsulator.setFactory(new SolsticeEntityEncapsulatorFactory());

		HyperionScheduler.getScheduler().setApplication(this);
		onSchedulerInitialized();

		getMarkupSettings().setStripWicketTags(true);

		Heinlein.init(this, "css/beholder-web.css");

		Slack.integration.initialize(this, "");

		FontAwesomeInitializer.get().initialize(this);

		mountPage("views/${identifier}", MapViewPage.class);
		mountPage("music/${identifier}", MusicPage.class);

		webSocketRegistry = new SimpleWebSocketConnectionRegistry();

		RollBarData data = ctx.getBean(RollBarData.class);

		if (data != null && data.getClientKey() != null) {
			//			BeholderApplication.get().getHeaderContributorListeners()
			//					.add(new RollbarClientListener(data.getClientKey(), data.getEnvironment()));
		}

		ApplicationMetadataStore metadata = ctx.getBean(ApplicationMetadataStore.class);

		getApplicationListeners().add(new RollbarDeployListener(data, metadata));
		getApplicationListeners().add(new OnDeploySlackNotifier(ctx.getBean(
				DeployNotificationContext.class), metadata, getServletContext()));
		getApplicationSettings().setInternalErrorPage(InternalErrorPage.class);
		getApplicationSettings().setPageExpiredErrorPage(PageExpiredPage.class);
		getRequestCycleListeners().add(new IRequestCycleListener() {
			@Override
			public IRequestHandler onException(RequestCycle cycle,
				Exception ex) {
				RollBarReference.instance.errorCaught(ex);

				return cycle.getActiveRequestHandler();
			}
		});
		HyperionApp.get().setApplicationVersion(getRevision());

		getJavaScriptLibrarySettings().setJQueryReference(JQueryResourceReference.getV3());
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

	public void onSchedulerInitialized() {
		HyperionScheduler.getScheduler()
			.scheduleTask(DateTime.now(), new InitializeCompendiumJob());

	}

	public String getRevision() {
		return ResourceUtil.readResourceAsString(BeholderApplication.class, "revision.txt").getOrElse("unknown");
	}

	public String getCommitMessage() {
		return ResourceUtil.readResourceAsString(BeholderApplication.class, "commit.txt").getOrElse("");
	}

	public String getCommitDetails() {
		return ResourceUtil.readResourceAsString(BeholderApplication.class, "commit-notes.txt").getOrNull();
	}
}
