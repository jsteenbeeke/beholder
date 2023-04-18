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
package com.jeroensteenbeeke.topiroll.beholder.util.compendium;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CompendiumParserTest {
	private static final String TEST_INPUT = "name: Alarm\n" +
			"level: 1\n" +
			"school: abjuration\n" +
			"\n" +
			"# Alarm \n" +
			"_1st‐level abjuration (ritual)_ \n" +
			"\n" +
			"**Casting Time:** 1 minute    \n" +
			"**Range:** 30 feet    \n" +
			"**Components:** V, S, M (a tiny bell and a piece of fine silver wire)    \n" +
			"**Duration:** 8 hours \n" +
			"\n" +
			"You set an alarm against unwanted intrusion. Choose a door, a window, or an area within range that is no larger than a 20-foot cube. Until the spell ends, an alarm alerts you whenever a" +
			" Tiny or larger creature touches or enters the warded area. When you cast the spell, you can designate creatures that won't set off the alarm. You also choose whether the alarm is " +
			"mental or audible.    \n" +
			"A mental alarm alerts you with a ping in your mind if you are within 1 mile of the warded area. This ping awakens you if you are sleeping.    \n" +
			"An audible alarm produces the sound of a hand bell for 10 seconds within 60 feet.\n"+
			"\n"+
			"| Table | Test |\n"+
			"|-------|------|\n"+
			"| Cell  | Cell |";


	@Test
	public void testCompendiumParseLogic() {
		Compendium.HtmlOutput output = Compendium.textToHtml(TEST_INPUT);
		assertEquals("Alarm", output.getTitle());

		assertTrue(output.getText().contains("<h1>Alarm</h1>"), output.getText());
		assertTrue(output.getText().contains("<th>Table</th>"), output.getText());
		assertTrue(output.getText().contains("<th>Test</th>"), output.getText());
		assertTrue(output.getText().contains("<td>Cell</td>"), output.getText());

		System.out.println(output.getText());
	}
}
