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
