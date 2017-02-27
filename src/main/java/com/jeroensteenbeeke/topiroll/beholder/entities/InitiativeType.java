/**
 * This file is part of Beholder
 * (C) 2016 Jeroen Steenbeeke
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
