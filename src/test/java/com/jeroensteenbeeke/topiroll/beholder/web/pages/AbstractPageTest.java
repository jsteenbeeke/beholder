/*
 * This file is part of Beholder
 * Copyright (C) 2016 - 2023 Jeroen Steenbeeke
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
package com.jeroensteenbeeke.topiroll.beholder.web.pages;

import com.jeroensteenbeeke.hyperion.solitary.InMemory;
import com.jeroensteenbeeke.topiroll.beholder.BeholderApplication;
import com.jeroensteenbeeke.topiroll.beholder.beans.IdentityService;
import com.jeroensteenbeeke.topiroll.beholder.beans.data.UserDescriptor;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.frontend.StartBeholderApplication;
import com.jeroensteenbeeke.topiroll.beholder.web.BeholderSession;
import org.apache.wicket.Application;
import org.apache.wicket.protocol.http.mock.MockHttpServletRequest;
import org.apache.wicket.protocol.http.mock.MockHttpSession;
import org.apache.wicket.protocol.http.mock.MockServletContext;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class AbstractPageTest {
	private static final String APPLICATION_KEY = "wicket.BeholderApplication";


	private InMemory.Handler handler;

	protected WicketTester wicketTester;

	private BeholderApplication application;

	private EntityManagerFactory entityManagerFactory;

	@BeforeEach
	public void startApplication() throws Exception {
		handler = StartBeholderApplication
			.createApplicationHandler(new String[0]).orElseThrow(IllegalStateException::new);

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


	protected void login() {
		BeholderUser beholderUser = application.getBean(IdentityService.class).getOrCreateUser(
			new UserDescriptor().setAccessToken("I_bet_she_could_succubus")
				.setAvatar("http://localhost:8081/beholder/img/logo48.png").setTeamId("31337")
				.setTeamName("Topiroll").setUserId("1337")
				.setUserName(System.getProperty("user.name")));

		BeholderSession.get().setUser(beholderUser);
	}


	@AfterEach
	public void endRequest() {
		TransactionSynchronizationManager.unbindResource(entityManagerFactory);

		RequestContextHolder.resetRequestAttributes();
		
		if (handler != null) {
			handler.terminate();
		}
	}
}
