package com.jeroensteenbeeke.topiroll.beholder;

import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
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
import com.jeroensteenbeeke.hyperion.tardis.scheduler.HyperionScheduler;
import com.jeroensteenbeeke.topiroll.beholder.web.BeholderSession;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.HomePage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.MapViewPage;
import com.jeroensteenbeeke.topiroll.beholder.web.resources.ToScaleMapResource;

public class BeholderApplication extends WebApplication implements
	ApplicationContextProvider {
	private ApplicationContext ctx;
	
	@Override
	protected void init() {
		super.init();

		getComponentInstantiationListeners().add(
				new SpringComponentInjector(this));
		ctx = WebApplicationContextUtils
				.getWebApplicationContext(getServletContext());

		EntityEncapsulator.setFactory(new SolsticeEntityEncapsulatorFactory());

		HyperionScheduler.getScheduler().setApplication(this);

		getMarkupSettings().setStripWicketTags(true);

		Heinlein.init(this, "css/beholder-web.css");
		
		Slack.integration.initialize(this, "");
		
		mountPage("views/${identifier}", MapViewPage.class);
		mountResource("maps/${noise}/${viewId}", new ResourceReference(BeholderApplication.class, "maps") {
			private static final long serialVersionUID = 1L;

			@Override
			public IResource getResource() {
				
				
				return new ToScaleMapResource();
			}
		});
		
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