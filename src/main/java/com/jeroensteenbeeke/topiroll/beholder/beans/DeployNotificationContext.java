package com.jeroensteenbeeke.topiroll.beholder.beans;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public interface DeployNotificationContext {
	@CheckForNull
	String getDeployWebhook();

	@Nonnull
	String getEnvironmentName();

	@CheckForNull
	String getDeployingInstance();
}
