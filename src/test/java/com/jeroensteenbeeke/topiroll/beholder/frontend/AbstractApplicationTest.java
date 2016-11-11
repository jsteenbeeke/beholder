package com.jeroensteenbeeke.topiroll.beholder.frontend;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.jeroensteenbeeke.hyperion.solitary.InMemory.Handler;

public abstract class AbstractApplicationTest {
	private static Handler handler;

	@BeforeClass
	public static void startApplication() throws Exception {
		handler = StartBeholderApplication.createApplicationHandler(new String[0]).orElseThrow(
				() -> new IllegalStateException("Could not start webserver"));
	}
	
	@AfterClass
	public static void closeApplication() throws Exception {
		handler.terminate();
	}
}