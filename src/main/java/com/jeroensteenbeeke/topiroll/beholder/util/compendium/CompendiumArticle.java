/*
 * This file is part of Beholder
 * Copyright (C) 2016 - 2023 Jeroen Steenbeeke
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
package com.jeroensteenbeeke.topiroll.beholder.util.compendium;

import java.util.Objects;

public class CompendiumArticle {
	private final String sourceText;

	private final String htmlText;

	public CompendiumArticle(String sourceText, String htmlText) {
		this.sourceText = sourceText;
		this.htmlText = htmlText;
	}

	public String getSourceText() {
		return sourceText;
	}

	public String getHtmlText() {
		return htmlText;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CompendiumArticle that = (CompendiumArticle) o;
		return Objects.equals(sourceText, that.sourceText) &&
				Objects.equals(htmlText, that.htmlText);
	}

	@Override
	public int hashCode() {

		return Objects.hash(sourceText, htmlText);
	}
}
