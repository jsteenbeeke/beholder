package com.jeroensteenbeeke.topiroll.beholder.util.compendium;

import com.jeroensteenbeeke.topiroll.beholder.util.compendium.parser.HtmlListener;
import com.jeroensteenbeeke.topiroll.beholder.util.compendium.parser.ReStructuredTextLexer;
import com.jeroensteenbeeke.topiroll.beholder.util.compendium.parser.ReStructuredTextParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.apache.wicket.util.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassRelativeResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;

public enum Compendium {
	INSTANCE;

	private static final Logger log = LoggerFactory.getLogger(Compendium.class);

	private final Map<CompendiumIndexEntry, CompendiumArticle> articles;

	Compendium() {
		this.articles = new TreeMap<>();
	}

	public void init() {
		ClassRelativeResourceLoader loader = new ClassRelativeResourceLoader(Compendium.class);
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(loader);

		log.info("Scanning for RST files");

		try {
			Resource[] resources = resolver.getResources("open5e/source/**/*.rst");

			for (Resource res : resources) {
				String ref = res.getURL().toString();

				if (ref.contains("open5e")) {
					ref = ref.substring(ref.indexOf("open5e")+7);
				}

				if (res.isReadable()) {
					try {

						String source = IOUtils.toString(res.getInputStream(), "UTF-8");
						HtmlOutput output = textToHtml(ref, source);

						CompendiumIndexEntry index = new CompendiumIndexEntry(ref, output.getTitle());
						CompendiumArticle article = new CompendiumArticle(source, output.getText());

						articles.put(index, article);

						log.info("\t- {} @ {}", index.getTitle(), index.getPath());
						log.info("\t\t{}", article.getHtmlText());
					} catch (ParseCancellationException | IOException ioe) {
						log.error("\tCould not parse {}", ref);
					}
				}
			}
			log.info("Found {} RST files", resources.length);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}

	}

	static HtmlOutput textToHtml(String path, String source) {
		AtomicReference<String> ref = new AtomicReference<>("");

		StringBuilder output = new StringBuilder();
		ReStructuredTextParser parser = new ReStructuredTextParser(
				new BufferedTokenStream(
						new ReStructuredTextLexer(
								new ANTLRInputStream(source))));
		parser.setErrorHandler(new BailErrorStrategy());
		parser.addParseListener(new HtmlListener(output) {
			@Override
			public void exitTitle(ReStructuredTextParser.TitleContext ctx) {
				super.exitTitle(ctx);

				if (ctx.textStart() != null) {
					ref.compareAndSet("", ctx.textStart().getText());
				}
			}
		});

		parser.parse();

		return new HtmlOutput(ref.get(), output.toString());
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
