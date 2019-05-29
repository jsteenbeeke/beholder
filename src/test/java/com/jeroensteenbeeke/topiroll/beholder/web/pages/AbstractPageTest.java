package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import com.jeroensteenbeeke.hyperion.solitary.InMemory;
import com.jeroensteenbeeke.topiroll.beholder.BeholderApplication;
import com.jeroensteenbeeke.topiroll.beholder.beans.IdentityService;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.frontend.StartBeholderApplication;
import com.jeroensteenbeeke.topiroll.beholder.web.BeholderSession;
import com.jeroensteenbeeke.topiroll.beholder.web.components.OnClickBehavior;
import org.apache.wicket.Application;
import org.apache.wicket.protocol.http.mock.MockHttpServletRequest;
import org.apache.wicket.protocol.http.mock.MockHttpSession;
import org.apache.wicket.protocol.http.mock.MockServletContext;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractPageTest {
	private static final String APPLICATION_KEY = "wicket.BeholderApplication";


	private static InMemory.Handler handler;

	protected WicketTester wicketTester;

	private BeholderApplication application;

	private EntityManagerFactory entityManagerFactory;

	@BeforeClass
	public static void startApplication() throws Exception {
		handler = StartBeholderApplication
			.createApplicationHandler(new String[0]).orElseThrow(IllegalStateException::new);
	}

	protected void login() {
		BeholderUser beholderUser = application.getBean(IdentityService.class).getOrCreateUser(
			new IdentityService.UserDescriptor().setAccessToken("I_bet_she_could_succubus")
				.setAvatar("http://localhost:8081/beholder/img/logo48.png").setTeamId("31337")
				.setTeamName("Topiroll").setUserId("1337")
				.setUserName(System.getProperty("user.name")));

		BeholderSession.get().setUser(beholderUser);
	}

	@Before
	public void createTester() {
		application = (BeholderApplication) Application.get(APPLICATION_KEY);
		wicketTester = new WicketTester(application, false);

		MockServletContext sctx = new MockServletContext(
			wicketTester.getApplication(), "/src/main/webapp/");
		MockHttpServletRequest request = new MockHttpServletRequest(
			wicketTester.getApplication(), new MockHttpSession(sctx), sctx);
		RequestAttributes attr = new ServletRequestAttributes(request);

		RequestContextHolder.setRequestAttributes(attr);

		ApplicationContext context = BeholderApplication.get()
			.getApplicationContext();
		entityManagerFactory = context.getBean(EntityManagerFactory.class);
		EntityManager em = context.getBean(EntityManager.class);
		EntityManagerHolder emHolder = new EntityManagerHolder(em);
		TransactionSynchronizationManager.bindResource(entityManagerFactory, emHolder);
	}

	@After
	public void endRequest() {
		TransactionSynchronizationManager.unbindResource(entityManagerFactory);

		RequestContextHolder.resetRequestAttributes();
	}

	@AfterClass
	public static void shutdownApplication() throws Exception {
		handler.terminate();
	}
}
