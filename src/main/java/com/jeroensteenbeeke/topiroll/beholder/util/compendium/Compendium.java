/**
 * This file is part of Beholder
 * (C) 2016-2019 Jeroen Steenbeeke
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

import com.google.common.collect.ImmutableMap;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.options.MutableDataSet;
import org.apache.wicket.util.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassRelativeResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class Compendium {
	private static final Logger log = LoggerFactory.getLogger(Compendium.class);

	private static final Parser parser = initParser();

	private static Parser initParser() {
		MutableDataSet options = new MutableDataSet();

		options.set(Parser.PARSER_EMULATION_PROFILE, ParserEmulationProfile.GITHUB);

		return Parser.builder(options).extensions(Arrays.asList(TablesExtension.create(), StrikethroughExtension.create())).build();
	}

	private static final HtmlRenderer renderer = initRenderer();

	private static HtmlRenderer initRenderer() {
		MutableDataSet options = new MutableDataSet();

		options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");

		return HtmlRenderer.builder(options).extensions(Arrays.asList(TablesExtension.create(), StrikethroughExtension.create())).build();

	}

	public static Map<CompendiumIndexEntry, CompendiumArticle> scanArticles() {
		ImmutableMap.Builder<CompendiumIndexEntry, CompendiumArticle> articles = ImmutableMap.builder();

		ClassRelativeResourceLoader loader = new ClassRelativeResourceLoader(Compendium.class);
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(loader);

		log.info("Scanning for MD files");

		try {
			Resource[] resources = resolver.getResources("5thsrd/docs/**/*.md");

			for (Resource res : resources) {
				String ref = res.getURL().toString();

				if (ref.contains("5thsrd")) {
					ref = ref.substring(ref.indexOf("5thsrd") + 7);
				}

				if (res.isReadable()) {
					try {

						String source = IOUtils.toString(res.getInputStream(), "UTF-8");
						HtmlOutput output = textToHtml(source);

						CompendiumIndexEntry index = new CompendiumIndexEntry(ref, output.getTitle());
						CompendiumArticle article = new CompendiumArticle(source, output.getText());

						articles.put(index, article);
					} catch (IOException ioe) {
						log.error("\tCould not parse {}", ref);
					}
				}
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}

		ImmutableMap<CompendiumIndexEntry, CompendiumArticle> result = articles.build();

		log.info("Found {} MD files", result.size());

		return result;
	}

	public static HtmlOutput textToHtml(String source) {
		AtomicReference<String> ref = new AtomicReference<>("");

		Node document = parser.parse(source);
		String html = renderer.render(document);

		for (Element h1 : Jsoup.parse(html).getElementsByTag("h1")) {
			ref.set(h1.text());
			break;
		}

		return new HtmlOutput(ref.get(), html);
	}

	public static class HtmlOutput {
		private final String title;

		private final String text;

		HtmlOutput(String title, String text) {
			this.title = title;
			this.text = text;
		}

		public String getTitle() {
			return title;
		}

		public String getText() {
			return text;
		}
	}
}
