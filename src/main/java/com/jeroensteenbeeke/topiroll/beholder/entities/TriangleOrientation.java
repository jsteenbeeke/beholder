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

import com.google.common.collect.ImmutableList;
import com.jeroensteenbeeke.topiroll.beholder.web.data.shapes.XY;

import java.awt.Polygon;
import java.util.List;

public enum TriangleOrientation {
	TopLeft("Top left") {
		@Override
		public void renderCSS(StringBuilder builder) {
			builder.append("clip-path: polygon(0% 0%, 0% 100%, 100% 0%);");
			builder.append("clip-path: url('#triangleTopLeft');");
			builder.append("-webkit-clip-path: polygon(0% 0%, 0% 100%, 100% 0%);");
		}

		@Override
		public List<XY> toPolygon(int left, int top, int width, int height) {
			int[] x = { left, left + width, left };
			int[] y = { top, top, top + height };

			return xyList(x, y);

		}
	},
	TopRight("Top right") {
		@Override
		public void renderCSS(StringBuilder builder) {
			builder.append("clip-path: polygon(0% 0%, 100% 0%, 100% 100%);");
			builder.append("clip-path: url('#triangleTopRight');");
			builder.append("-webkit-clip-path: polygon(0% 0%, 100% 0%, 100% 100%);");
		}

		@Override
		public List<XY> toPolygon(int left, int top, int width, int height) {
			int[] x = { left, left + width, left + width };
			int[] y = { top, top, top + height };

			return xyList(x, y);
		}
	},
	BottomLeft("Bottom left") {
		@Override
		public void renderCSS(StringBuilder builder) {
			builder.append("clip-path: polygon(0% 0%, 0% 100%, 100% 100%);");
			builder.append("clip-path: url('#triangleBottomLeft');");
			builder.append("-webkit-clip-path: polygon(0% 0%, 0% 100%, 100% 100%);");
		}

		@Override
		public List<XY> toPolygon(int left, int top, int width, int height) {
			int[] x = { left, left, left + width };
			int[] y = { top + height, top, top + height };

			return xyList(x, y);
		}
	},
	BottomRight("Bottom right") {
		@Override
		public void renderCSS(StringBuilder builder) {
			builder.append("clip-path: polygon(100% 0%, 0% 100%, 100% 100%);");
			builder.append("clip-path: url('#triangleBottomRight');");
			builder.append("-webkit-clip-path: polygon(100% 0%, 0% 100%, 100% 100%);");
		}

		@Override
		public List<XY> toPolygon(int left, int top, int width, int height) {
			int[] x = { left, left + width, left + width };
			int[] y = { top + height, top, top + height };

			return xyList(x, y);
		}
	};

	protected List<XY> xyList(int[] x, int[] y) {
		if (x.length != y.length) {
			throw new IllegalArgumentException("Arrays should be equal in length");
		}

		ImmutableList.Builder<XY> builder = ImmutableList.builder();
		for (int i = 0; i < x.length; i++) {
			builder.add(new XY(x[i], y[i]));
		}
		return builder.build();
	}

	private final String description;

	private TriangleOrientation(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public abstract void renderCSS(StringBuilder builder);

	public abstract List<XY> toPolygon(int left, int top, int width, int height);
}
