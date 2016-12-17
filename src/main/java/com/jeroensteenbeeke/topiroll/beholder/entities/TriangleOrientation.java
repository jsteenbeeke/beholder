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
package com.jeroensteenbeeke.topiroll.beholder.entities;

import java.awt.Polygon;

public enum TriangleOrientation {
	TopLeft("Top left") {
		@Override
		public void renderCSS(StringBuilder builder) {
			builder.append("clip-path: polygon(0% 0%, 0% 100%, 100% 0%);");
			builder.append("clip-path: url('#triangleTopLeft');");
		}

		@Override
		public Polygon toPolygon(int left, int top, int width, int height) {
			int[] x = { left, left + width, left };
			int[] y = { top, top, top + height };

			return new Polygon(x, y, 3);

		}
	},
	TopRight("Top right") {
		@Override
		public void renderCSS(StringBuilder builder) {
			builder.append("clip-path: polygon(0% 0%, 100% 0%, 100% 100%);");
			builder.append("clip-path: url('#triangleTopRight');");

		}

		@Override
		public Polygon toPolygon(int left, int top, int width, int height) {
			int[] x = { left, left + width, left + width };
			int[] y = { top, top, top + height };

			return new Polygon(x, y, 3);
		}
	},
	BottomLeft("Bottom left") {
		@Override
		public void renderCSS(StringBuilder builder) {
			builder.append("clip-path: polygon(0% 0%, 0% 100%, 100% 100%);");
			builder.append("clip-path: url('#triangleBottomLeft');");
		}

		@Override
		public Polygon toPolygon(int left, int top, int width, int height) {
			int[] x = { left, left, left + width };
			int[] y = { top + height, top, top + height };

			return new Polygon(x, y, 3);
		}
	},
	BottomRight("Bottom right") {
		@Override
		public void renderCSS(StringBuilder builder) {
			builder.append("clip-path: polygon(100% 0%, 0% 100%, 100% 100%);");
			builder.append("clip-path: url('#triangleBottomRight');");
		}

		@Override
		public Polygon toPolygon(int left, int top, int width, int height) {
			int[] x = { left, left + width, left + width };
			int[] y = { top + height, top, top + height };

			return new Polygon(x, y, 3);
		}
	};

	private final String description;

	private TriangleOrientation(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public abstract void renderCSS(StringBuilder builder);

	public abstract Polygon toPolygon(int left, int top, int width, int height);
}
