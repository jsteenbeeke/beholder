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
package com.jeroensteenbeeke.topiroll.beholder;

import com.jeroensteenbeeke.hyperion.Hyperion;
import com.jeroensteenbeeke.hyperion.rollbar.RollBarDeployNotifier;
import com.jeroensteenbeeke.hyperion.solstice.spring.ApplicationMetadataStore;
import com.jeroensteenbeeke.topiroll.beholder.beans.RollBarData;
import io.vavr.control.Option;
import org.apache.wicket.Application;
import org.apache.wicket.IApplicationListener;

public class RollbarDeployListener implements IApplicationListener {
	private final RollBarData data;

	private final ApplicationMetadataStore metadataStore;

	RollbarDeployListener(RollBarData data, ApplicationMetadataStore metadataStore) {
		this.data = data;
		this.metadataStore = metadataStore;
	}

	@Override
	public void onAfterInitialized(Application application) {
		if (data.getEnvironment() != null) {
			final String runningApplicationHash = String.format("%s:%s:%s",
																BeholderApplication.get().getRevision(),
																Hyperion.getRevision().getOrElse("Unknown"),
																Option.of(System.getenv("DOCKER_IMAGE_ID")).getOrElse("Unknown")
			);

			Option<String> previousApplicationHash = metadataStore.readString(BeholderApplication.KEY_BEHOLDER_CURRENT_VERSION);

			if (previousApplicationHash.isEmpty() || previousApplicationHash
				.filter(p -> !p.equals(runningApplicationHash)).isDefined()) {
				RollBarDeployNotifier.createNotifier().withApiKey(data.getServerKey())
									 .withEnvironment(data.getEnvironment())
									 .andDeployingUser(data.getLocalUsername())
									 .notifyDeploy(BeholderApplication.get().getRevision(), Hyperion
										 .getRevision()
										 .map(rev -> "Hyperion version: " + rev)
										 .getOrElse("Hyperion version: unknown")
										 .concat(Hyperion
													 .getCommitTitle()
													 .map("; "::concat)
													 .getOrElse(""))
									 );
			}
		}
	}

	@Override
	public void onBeforeDestroyed(Application application) {

	}
}
