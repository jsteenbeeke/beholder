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
/**
 *     This file is part of Beholder
 *     (C) 2016 Jeroen Steenbeeke
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jeroensteenbeeke.topiroll.beholder.entities;

import java.awt.Color;

public enum TokenBorderIntensity {
	HEALTHY("Healthy", 100), MINOR_INJURIES("Slightly injured",
			75), MODERATELY_INJURED("Moderately injured",
					50), HEAVILY_INJURED("Heavily injured", 25), DEAD("Dead", 0);

	private final String description;

	private final int percentage;

	private TokenBorderIntensity(String description, int percentage) {
		this.description = description;
		this.percentage = percentage;
	}

	public String getColor(TokenBorderType type) {
		String red = hex(type.getRed() * percentage / 100, 2);
		String green = hex(type.getGreen() * percentage / 100, 2);
		String blue = hex(type.getBlue() * percentage / 100, 2);

		return String.format("#%s%s%s", red, green, blue);
	}

	public String hex(int value, int length) {
		if (length <= 0) {
			throw new IllegalArgumentException("Length must be positive");
		}

		if (length > 7) {
			throw new IllegalArgumentException(
					"Length must be 7 or less to prevent overflows");
		}

		int max = (int) Math.pow(16, length);

		if (value < 0) {
			throw new IllegalArgumentException("Values must be non-negative");
		}

		if (value > max) {
			throw new IllegalArgumentException(String.format(
					"Values with length %d cannot be greater than %d", length,
					max));
		}

		String raw = Integer.toHexString(value);

		if (raw.length() > length) {
			return raw.substring(raw.length() - length, raw.length());
		}

		while (raw.length() < length) {
			raw = "0".concat(raw);
		}

		return raw;
	}

	public Color toColor(TokenBorderType borderType) {
		int red = Math.min(255,
				Math.max(0, borderType.getRed() * percentage / 100));
		int green = Math.min(255,
				Math.max(0, borderType.getGreen() * percentage / 100));
		int blue = Math.min(255,
				Math.max(0, borderType.getBlue() * percentage / 100));

		return new Color(red, green, blue);
	}

	public String getDescription() {
		return description;
	}

}
