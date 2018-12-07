package com.jeroensteenbeeke.topiroll.beholder.jobs;

import com.jeroensteenbeeke.hyperion.tardis.scheduler.HyperionTask;
import com.jeroensteenbeeke.hyperion.tardis.scheduler.ServiceProvider;
import com.jeroensteenbeeke.lux.TypedResult;
import com.jeroensteenbeeke.topiroll.beholder.Jobs;
import com.jeroensteenbeeke.topiroll.beholder.beans.CompendiumService;
import com.jeroensteenbeeke.topiroll.beholder.entities.CompendiumEntry;
import com.jeroensteenbeeke.topiroll.beholder.util.compendium.Compendium;
import com.jeroensteenbeeke.topiroll.beholder.util.compendium.CompendiumArticle;
import com.jeroensteenbeeke.topiroll.beholder.util.compendium.CompendiumIndexEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class InitializeCompendiumJob extends HyperionTask {
	private static final Logger log = LoggerFactory.getLogger(InitializeCompendiumJob.class);

	public InitializeCompendiumJob() {
		super("Ínitialize Compendium by reading MD files", Jobs.Initialize);
	}

	@Override
	public void run(ServiceProvider provider) {
		Map<CompendiumIndexEntry, CompendiumArticle> articles = Compendium.scanArticles();
		CompendiumService compendiumService = provider.getService(CompendiumService.class);

		articles.forEach((k,v) -> {
			if (!compendiumService.articleExists(k.getPath())) {
				TypedResult<CompendiumEntry> result = compendiumService.createImportedArticle(k.getTitle(), k.getPath(), v.getHtmlText());
				if (!result.isOk()) {
					log.warn("Could not create entry for {}: {}", k.getPath(), result.getMessage());
				}
			}
		});

	}
}
