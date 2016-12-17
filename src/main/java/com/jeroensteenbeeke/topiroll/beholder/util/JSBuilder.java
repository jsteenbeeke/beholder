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

	public JSBuilder tryBlockWithConsoleLog() {
		__("try {");
		
		return new JSBuilder("\t".concat(indent), builder, this) {
			@Override
			protected void terminateCurrent() {
				if (parent != null) {
					builder.append(parent.getIndent());
				}

				builder.append("\t} catch (e) {\n");

				if (parent != null) {
					builder.append(parent.getIndent());
				}

				builder.append("\t\tconsole.log(e.message, e.name);\n");
				
				if (parent != null) {
					builder.append(parent.getIndent());
				}

				
				builder.append("\t}\n");
			}
		};
		
	}
}
