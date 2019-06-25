/**
 * This file is part of Beholder
 * (C) 2016-2019 Jeroen Steenbeeke
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
import com.jeroensteenbeeke.topiroll.beholder.beans.RollBarData;
import org.apache.wicket.Application;
import org.apache.wicket.IApplicationListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class RollbarDeployListener implements IApplicationListener {
	private final RollBarData data;

	RollbarDeployListener(RollBarData data) {
		this.data = data;
	}

	@Override
	public void onAfterInitialized(Application application) {
		if (data.getEnvironment() != null) {
			RollBarDeployNotifier.createNotifier().withApiKey(data.getServerKey())
								 .withEnvironment(data.getEnvironment())
								 .andDeployingUser(data.getLocalUsername())
								 .notifyDeploy(BeholderApplication.get().getRevision(), Hyperion
									 .getRevision()
									 .map(rev -> "Hyperion version: " + rev)
									 .getOrElse("Hyperion version: unknown")
											   .concat(Hyperion.getCommitTitle().map("; "::concat).getOrElse(""))
								 );
		}
	}

	@Override
	public void onBeforeDestroyed(Application application) {

	}
}
