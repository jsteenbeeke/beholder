package com.jeroensteenbeeke.topiroll.beholder.util;

import java.util.Arrays;
import java.util.stream.Collectors;

public class JSBuilder {
	private final String indent;

	private final StringBuilder builder;

	private final JSBuilder parent;

	private JSBuilder(String indent, StringBuilder builder, JSBuilder parent) {
		this.indent = indent;
		this.builder = builder;
		this.parent = parent;
	}

	public String getIndent() {
		return indent;
	}

	public JSBuilder __(String statement, Object... params) {
		builder.append(indent);
		if (params.length == 0) {
			builder.append(statement);
		} else {
			builder.append(String.format(statement, params));
		}
		builder.append("\n");
		return this;
	}

	public JSBuilder ifBlock(String condition, Object... params) {
		if (params.length == 0) {
			__("if (%s) {", condition);
		} else {
			__("if (%s) {", String.format(condition, params));
		}
		return new JSBuilder("\t".concat(indent), builder, this) {
			@Override
			protected void terminateCurrent() {
				if (parent != null) {
					builder.append(parent.getIndent());
				}

				builder.append("}\n");
			}
		};
	}

	public JSBuilder varFunction(String varName, String... params) {
		__("var %s = function(%s) {", varName,
				Arrays.stream(params).collect(Collectors.joining(", ")));
		return new JSBuilder("\t".concat(indent), builder, this) {
			@Override
			protected void terminateCurrent() {
				builder.append(parent.getIndent());
				builder.append("}\n");
			}
		};
	}

	public JSBuilder objFunction(String objName, String... params) {
		__("%s = function(%s) {", objName,
				Arrays.stream(params).collect(Collectors.joining(", ")));
		return new JSBuilder("\t".concat(indent), builder, this) {
			@Override
			protected void terminateCurrent() {
				builder.append(parent.getIndent());
				builder.append("}\n");
			}
		};
	}

	public JSBuilder close() {
		if (parent == null) {
			return this;
		}

		terminateCurrent();

		return parent;
	}

	@Override
	public String toString() {
		if (parent == null) {
			return builder.toString();
		}

		terminateCurrent();

		return parent.toString();
	}

	protected void terminateCurrent() {

	}

	public static JSBuilder create() {
		return new JSBuilder("", new StringBuilder(), null);
	}
}
