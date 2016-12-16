package com.jeroensteenbeeke.topiroll.beholder.entities;

import java.awt.Polygon;

public enum TriangleOrientation {
	TopLeft("Top left") {
		@Override
		public void renderCSS(StringBuilder builder, int sides) {

			builder.append("border-top: ").append(sides)
					.append("px solid rgba(255, 0, 0, 0.5);");
			builder.append("border-right: ").append(sides)
					.append("px solid transparent;");

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
		public void renderCSS(StringBuilder builder, int sides) {
			builder.append("border-top: ").append(sides)
					.append("px solid rgba(255, 0, 0, 0.5);");
			builder.append("border-left: ").append(sides)
					.append("px solid transparent;");
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
		public void renderCSS(StringBuilder builder, int sides) {
			builder.append("border-bottom: ").append(sides)
					.append("px solid rgba(255, 0, 0, 0.5);");
			builder.append("border-right: ").append(sides)
					.append("px solid transparent;");
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
		public void renderCSS(StringBuilder builder, int sides) {
			builder.append("border-bottom: ").append(sides)
					.append("px solid rgba(255, 0, 0, 0.5);");
			builder.append("border-left: ").append(sides)
					.append("px solid transparent;");
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

	public abstract void renderCSS(StringBuilder builder, int sides);

	public abstract Polygon toPolygon(int left, int top, int width, int height);
}
