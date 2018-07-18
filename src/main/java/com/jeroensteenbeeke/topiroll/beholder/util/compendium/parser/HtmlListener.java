package com.jeroensteenbeeke.topiroll.beholder.util.compendium.parser;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.HashMap;
import java.util.Map;

public class HtmlListener extends ReStructuredTextBaseListener {
	private final StringBuilder output;

	private final Map<Integer, String> headerClasses;

	private int headerTypeCounter = 1;

	public HtmlListener(StringBuilder output) {
		this.output = output;
		this.headerClasses = new HashMap<>();
	}

	@Override
	public void exitTitle(ReStructuredTextParser.TitleContext ctx) {
		super.exitTitle(ctx);

		ReStructuredTextParser.SectionContext parent = (ReStructuredTextParser.SectionContext) ctx.getParent();

		int marker = ctx.invokingState;

		if (!headerClasses.containsKey(marker)) {
			headerClasses.put(marker, String.format("h%d", headerTypeCounter++));
		}

		output.append(String.format("<%1$s>%2$s</%1$s>", headerClasses.get(marker),
				ctx.textStart().getText()));


	}

	@Override
	public void exitForcedText(ReStructuredTextParser.ForcedTextContext ctx) {
		super.exitForcedText(ctx);

		output.append(ctx.getText());
	}

	@Override
	public void exitText_fragment_start(ReStructuredTextParser.Text_fragment_startContext ctx) {
		super.exitText_fragment_start(ctx);

		output.append(ctx.getText());
	}

	@Override
	public void exitLineStart_fragment(ReStructuredTextParser.LineStart_fragmentContext ctx) {
		super.exitLineStart_fragment(ctx);

		output.append(ctx.getText());
	}

	@Override
	public void exitParagraph(ReStructuredTextParser.ParagraphContext ctx) {
		super.exitParagraph(ctx);

		output.append("<br />");
	}

	@Override
	public void visitTerminal(TerminalNode node) {
		super.visitTerminal(node);

		switch (node.getSymbol().getType()) {
			case ReStructuredTextLexer.LineBreak:
				break;
		}
	}
}
