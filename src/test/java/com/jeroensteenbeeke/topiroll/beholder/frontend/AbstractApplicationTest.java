/**
 * This file is part of Beholder
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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