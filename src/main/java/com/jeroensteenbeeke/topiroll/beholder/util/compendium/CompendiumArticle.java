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
