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
