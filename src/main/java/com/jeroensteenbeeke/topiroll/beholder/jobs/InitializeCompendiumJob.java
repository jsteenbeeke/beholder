package com.jeroensteenbeeke.topiroll.beholder.jobs;

import com.jeroensteenbeeke.hyperion.tardis.scheduler.HyperionTask;
import com.jeroensteenbeeke.hyperion.tardis.scheduler.ServiceProvider;
import com.jeroensteenbeeke.topiroll.beholder.Jobs;
import com.jeroensteenbeeke.topiroll.beholder.util.compendium.Compendium;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitializeCompendiumJob extends HyperionTask {
	private static final Logger log = LoggerFactory.getLogger(InitializeCompendiumJob.class);

	public InitializeCompendiumJob() {
		super("Ínitialize Compendium by reading RST files", Jobs.Initialize);
	}



	@Override
	public void run(ServiceProvider provider) {
		Compendium.INSTANCE.init();

	}
}
