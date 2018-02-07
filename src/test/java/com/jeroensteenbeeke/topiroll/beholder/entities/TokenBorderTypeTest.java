package com.jeroensteenbeeke.topiroll.beholder.entities;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
