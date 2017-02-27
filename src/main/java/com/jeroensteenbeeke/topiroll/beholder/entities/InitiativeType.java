package com.jeroensteenbeeke.topiroll.beholder.entities;

import java.util.Random;

public enum InitiativeType {
	Normal, Advantage {
		@Override
		public int determine(Random random, int bonus) {
			return Math.max(random.nextInt(20), random.nextInt(20)) + 1 + bonus;
		}
		
		@Override
		public String formatBonus(int bonus) {
			return super.formatBonus(bonus) + " (advantage)";
		}
	}, Disadvantage {
		@Override
		public int determine(Random random, int bonus) {
			return Math.min(random.nextInt(20), random.nextInt(20)) + 1 + bonus;
		}
		
		@Override
		public String formatBonus(int bonus) {
			return super.formatBonus(bonus) + " (disadvantage)";
		}
	};
	
	public int determine(Random random, int bonus) {
		return random.nextInt(20) + 1 + bonus;
	}

	public String formatBonus(int bonus) {
		if (bonus > 0) {
			return "+" + bonus;
		}

		return Integer.toString(bonus);
	}
}
