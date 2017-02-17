package com.jeroensteenbeeke.topiroll.beholder.jobs;

import com.jeroensteenbeeke.hyperion.tardis.scheduler.HyperionTask;
import com.jeroensteenbeeke.hyperion.tardis.scheduler.ServiceProvider;
import com.jeroensteenbeeke.topiroll.beholder.Jobs;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;

public class InitialRenderTask extends HyperionTask {
	private final long viewId;
	
	private final String sessionId;
	
	private final boolean previewMode;

	public  InitialRenderTask(long viewId, String sessionId, boolean previewMode) {
		super("Send a map update to a user", Jobs.Updates);
		this.viewId = viewId;
		this.sessionId = sessionId;
		this.previewMode = previewMode;
	}



	@Override
	public void run(ServiceProvider provider) {
		provider.getService(MapService.class).initializeView(viewId, sessionId, previewMode);
	}

}
