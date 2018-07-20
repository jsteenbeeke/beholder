package com.jeroensteenbeeke.topiroll.beholder.util.compendium;

import com.google.common.collect.ImmutableMap;
import org.apache.wicket.util.io.IOUtils;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Heading;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassRelativeResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;

public class Compendium {
	private static final Logger log = LoggerFactory.getLogger(Compendium.class);

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
					ref = ref.substring(ref.indexOf("5thsrd")+7);
				}

				if (res.isReadable()) {
					try {

						String source = IOUtils.toString(res.getInputStream(), "UTF-8");
						HtmlOutput output = textToHtml(source);

						CompendiumIndexEntry index = new CompendiumIndexEntry(ref, output.getTitle());
						CompendiumArticle article = new CompendiumArticle(source, output.getText());

						articles.put(index, article);

						log.info("\t- {} @ {}", index.getTitle(), index.getPath());
						log.info("\t\t{}", article.getHtmlText());
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

	static HtmlOutput textToHtml(String source) {
		AtomicReference<String> ref = new AtomicReference<>("");

		Parser parser = Parser.builder().build();
		Node document = parser.parse(source);
		HtmlRenderer renderer = HtmlRenderer.builder().build();

		document.accept(new AbstractVisitor() {
			@Override
			public void visit(Heading heading) {
				super.visit(heading);

				if (heading.getLevel() == 1) {
					heading.accept(new AbstractVisitor() {
						@Override
						public void visit(Text text) {
							super.visit(text);

							ref.compareAndSet("", text.getLiteral());
						}
					});
				}
			}
		});

		return new HtmlOutput(ref.get(), renderer.render(document));
	}

	static class HtmlOutput {
		private final String title;

		private final String text;

		public HtmlOutput(String title, String text) {
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