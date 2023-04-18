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
package com.jeroensteenbeeke.topiroll.beholder.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TokenBorderTypeTest {
	@Test
	public void testHexColors() {
		assertEquals("ff0000", TokenBorderType.Enemy.toHexColor().toLowerCase());
		assertEquals("808000", TokenBorderType.Neutral.toHexColor().toLowerCase());
		assertEquals("0000ff", TokenBorderType.Ally.toHexColor().toLowerCase());

		for (TokenBorderType type : TokenBorderType.values()) {
			assertEquals(6, type.toHexColor().length());
		}

	}
}
