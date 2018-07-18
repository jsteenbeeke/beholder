package com.jeroensteenbeeke.topiroll.beholder.util.compendium;

import com.jeroensteenbeeke.topiroll.beholder.util.compendium.Compendium;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CompendiumParserTest {
	private static final String TEST_INPUT = "\n" +
			".. _srd:mounted-combat:\n" +
			"\n" +
			"Mounted Combat\n" +
			"--------------\n" +
			"\n" +
			"A knight charging into battle on a warhorse, a wizard casting spells\n" +
			"from the back of a griffon, or a cleric soaring through the sky on a\n" +
			"pegasus all enjoy the benefits of speed and mobility that a mount can\n" +
			"provide.\n" +
			"\n" +
			"A willing creature that is at least one size larger than you and that\n" +
			"has an appropriate anatomy can serve as a mount, using the following\n" +
			"rules.\n" +
			"\n" +
			"Mounting and Dismounting\n" +
			"~~~~~~~~~~~~~~~~~~~~~~~~\n" +
			"\n" +
			"Once during your move, you can mount a creature that is within 5 feet of\n" +
			"you or dismount. Doing so costs an amount of movement equal to half your\n" +
			"speed. For example, if your speed is 30 feet, you must spend 15 feet of\n" +
			"movement to mount a horse. Therefore, you can't mount it if you don't\n" +
			"have 15 feet of movement left or if your speed is 0.\n" +
			"\n" +
			"If an effect moves your mount against its will while you're on it, you\n" +
			"must succeed on a DC 10 Dexterity saving throw or fall off the mount,\n" +
			"landing :ref:`srd:prone` in a space within 5 feet of it. If you're knocked :ref:`srd:prone`\n" +
			"while mounted, you must make the same saving throw.\n" +
			"\n" +
			"If your mount is knocked :ref:`srd:prone`, you can use your reaction to dismount it\n" +
			"as it falls and land on your feet. Otherwise, you are dismounted and\n" +
			"fall :ref:`srd:prone` in a space within 5 feet it.\n" +
			"\n" +
			"Controlling a Mount\n" +
			"~~~~~~~~~~~~~~~~~~~\n" +
			"\n" +
			"While you're mounted, you have two options. You can either control the\n" +
			"mount or allow it to act independently. Intelligent creatures, such as\n" +
			"dragons, act independently.\n" +
			"\n" +
			"You can control a mount only if it has been trained to accept a rider.\n" +
			"Domesticated horses, donkeys, and similar creatures are assumed to have\n" +
			"such training. The initiative of a controlled mount changes to match\n" +
			"yours when you mount it. It moves as you direct it, and it has only\n" +
			"three action options: Dash, Disengage, and Dodge. A controlled mount can\n" +
			"move and act even on the turn that you mount it.\n" +
			"\n" +
			"An independent mount retains its place in the initiative order. Bearing\n" +
			"a rider puts no restrictions on the actions the mount can take, and it\n" +
			"moves and acts as it wishes. It might flee from combat, rush to attack and devour a badly injured foe, or otherwise act against your wishes.\n" +
			"\n" +
			"In either case, if the mount provokes an opportunity attack while you're\n" +
			"on it, the attacker can target you or the mount.";

	@Test
	public void testCompendiumParseLogic() {
		Compendium.HtmlOutput output = Compendium.textToHtml("/test/test.rst", TEST_INPUT);
		assertEquals("Mounted Combat", output.getTitle());

		assertTrue(output.getText(), output.getText().contains("Mounting and Dismounting"));

		System.out.println(output.getText());
	}
}
