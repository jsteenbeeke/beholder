package com.jeroensteenbeeke.topiroll.beholder.web;

import com.jeroensteenbeeke.hyperion.annotation.Dataset;
import org.junit.Test;

@Dataset("slackcommand")
public class DoorbellTest extends SlackCommandTest {

	@Test
	public void ringDoorbell() throws Exception {
		issueCommand("/doorbell").expectingMessage("You rang the doorbell, but nobody is listening");
	}
}
