package com.jeroensteenbeeke.topiroll.beholder.web.components;

import com.jeroensteenbeeke.hyperion.ducktape.web.components.TypedPanel;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import org.apache.wicket.ajax.WicketEventJQueryResourceReference;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.UrlUtils;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import java.util.List;

public abstract class AbstractMapPreviewPanel extends TypedPanel<ScaledMap> {
	private WebMarkupContainer canvas;

	public AbstractMapPreviewPanel(String id, ScaledMap map) {
		super(id, ModelMaker.wrap(map));

		setOutputMarkupId(true);

		canvas = new WebMarkupContainer("preview");
		canvas.setOutputMarkupId(true);
		add(canvas);
	}

	@Override
	public void renderHead(IHeaderResponse response) {

		super.renderHead(response);

		response.render(JavaScriptHeaderItem
				.forReference(WicketEventJQueryResourceReference.get()));

		response.render(JavaScriptHeaderItem
				.forReference(new JavaScriptResourceReference(MapCanvas.class,
						"js/geometry.js")));

		response.render(JavaScriptHeaderItem
				.forReference(new JavaScriptResourceReference(MapCanvas.class,
						"js/previewcanvas.js")));

		StringBuilder js = new StringBuilder();

		js.append(String.format("renderMapToCanvas('%s', '%s/%d');", canvas.getMarkupId(),
						UrlUtils.rewriteToContextRelative("maps", getRequestCycle()), getModelObject().getId()));

		js.append(String.format("$('#%1$s > #dragdrop').css({\n" +
				"    \"position\" : \"absolute\",\n" +
				"    \"left\"     : $('#%2$s').position().left,\n" +
				"    \"top\"      : $('#%2$s').position().top\n" +
				"})", getMarkupId(), canvas.getMarkupId()));

		addOnDomReadyJavaScript(js);

		response.render(OnDomReadyHeaderItem.forScript(js));
	}

	protected abstract void addOnDomReadyJavaScript(StringBuilder js);
}
