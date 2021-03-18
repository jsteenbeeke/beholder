package com.jeroensteenbeeke.topiroll.beholder.beans;

import javax.annotation.CheckForNull;

public interface WebHookSupplier {
	@CheckForNull
	String getDeployWebhook();
}
