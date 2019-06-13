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
package com.jeroensteenbeeke.topiroll.beholder.web.components;

import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

public abstract class AbstractMapPreview extends Border implements ICoordinateTranslator {
	private static final long serialVersionUID = -5061786563617489387L;

	private final int desiredWidth;

	private final WebMarkupContainer dragdrop;

	protected WebMarkupContainer canvas;

	private final double factor;

	public AbstractMapPreview(String id, ScaledMap map) {
		this(id, map, map.getBasicWidth());
	}

	public AbstractMapPreview(String id, ScaledMap map, int desiredWidth) {
		super(id, ModelMaker.wrap(map));

		setOutputMarkupId(true);

		canvas = new WebMarkupContainer("preview");
		canvas.setOutputMarkupId(true);
		addToBorder(canvas);

		this.desiredWidth = desiredWidth;
		factor = (double) desiredWidth / (double) map.getBasicWidth();

		dragdrop = new WebMarkupContainer("dragdrop");
		dragdrop.setOutputMarkupId(true);
		addToBorder(dragdrop);


	}

	protected void renderMap(StringBuilder js) {
		js.append(String.format("renderMapToCanvas(document.multi%s, '%s', %d, function() {});\n\n", canvas.getMarkupId(),
				getMap().getImageUrl(), desiredWidth));
	}

	public WebMarkupContainer getDragdrop() {
		return dragdrop;
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
		// dragdrop.add(getBodyContainer());
	}

	public double getFactor() {
		return factor;
	}

	@Override
	public int translateToRealImageSize(int number) {
		return (int) (number / factor);
	}

	@Override
	public int translateToScaledImageSize(int number) {
		return (int) (number * factor);
	}

	protected ScaledMap getMap() {
		return (ScaledMap) getDefaultModelObject();
	}

	@Override
	public void renderHead(IHeaderResponse response) {

		super.renderHead(response);

		response.render(JavaScriptHeaderItem
				.forReference(new JavaScriptResourceReference(MapCanvas.class,
						"js/geometry.js")));

		response.render(JavaScriptHeaderItem
				.forReference(new JavaScriptResourceReference(MapCanvas.class,
						"js/images.js")));

		response.render(JavaScriptHeaderItem
				.forReference(new JavaScriptResourceReference(MapCanvas.class,
						"js/multicanvas.js")));

		response.render(JavaScriptHeaderItem
				.forReference(new JavaScriptResourceReference(MapCanvas.class,
						"js/previewcanvas.js")));
		long height = Math.round(getMap().getBasicHeight() * factor);

		StringBuilder js = new StringBuilder();

		js.append(String.format("document.multi%1$s = new MultiCanvas('%1$s', %2$d, %3$d);\n", canvas.getMarkupId(), desiredWidth, Math.round(height * 1.5)));
		js.append(String.format("renderMapToCanvas(document.multi%s, '%s', %d, function() { __PLACEHOLDER__ });\n\n", canvas.getMarkupId(),
				getMap().getImageUrl(), desiredWidth));

		StringBuilder onImageDrawComplete = new StringBuilder();

		onImageDrawComplete.append(String.format("\nvar dragDropOffset = document.getElementById('%1$s').getBoundingClientRect();\n", canvas.getMarkupId()));
		onImageDrawComplete.append(String.format("$('#%1$s > .dragdrop').css({\n" +
						"\t\"position\" : \"absolute\",\n" +
						"\t\"z-index\" :1,\n" +
						"\t\"left\"     : (dragDropOffset.left + window.pageXOffset),\n" +
						"\t\"top\"      : (dragDropOffset.top + window.pageYOffset),\n" +
						"\t\"width\"	: %3$d,\n" +
						"\t\"height\"	: %4$d,\n" +
						"});\n", getMarkupId(), canvas.getMarkupId(), (int) (desiredWidth * 1.5),
				(int) (height * 1.5 )));
		onImageDrawComplete.append("if (typeof renderListeners === 'array' || typeof " +
				"renderListeners === 'object') {\n");
		onImageDrawComplete.append("\tvar callback = function() {\n");
		onImageDrawComplete.append(String.format("\t\tvar canvasContainer = document" +
				".getElementById('%1$s');\n", canvas.getMarkupId()));
		onImageDrawComplete.append("\t\tif (canvasContainer !== null && typeof canvasContainer " +
				"=== \"object\") {\n");
		onImageDrawComplete.append("\t\t\tvar canvasContainerOffset = canvasContainer" +
				".getBoundingClientRect();\n");
		onImageDrawComplete.append(String.format("\t\t\tdocument.multi%s.recalculateOffset" +
						"(canvasContainerOffset" +
				".left + window.pageXOffset, canvasContainerOffset.top + window.pageYOffset);\n",
				canvas.getMarkupId()));
		onImageDrawComplete.append("\t\t}\n");
		onImageDrawComplete.append("\t}\n");
		onImageDrawComplete.append("\trenderListeners.push(callback);\n");
	    onImageDrawComplete.append("}\n");

		addOnDomReadyJavaScript("document.multi"+ canvas.getMarkupId(), onImageDrawComplete, factor);

		response.render(OnDomReadyHeaderItem
				.forScript(js.toString().replace("__PLACEHOLDER__", onImageDrawComplete.toString())));
	}

	protected abstract void addOnDomReadyJavaScript(String canvasId, StringBuilder js, double factor);

	public void refresh(AjaxRequestTarget target) {
		target.add(dragdrop);

		long height = Math.round(getMap().getBasicHeight() * factor);

		target.appendJavaScript(String.format("var dragDropOffset = document.getElementById('%1$s')" +
				".getBoundingClientRect();\n", canvas.getMarkupId()) + String.format("$('#%1$s > " +
						".dragdrop').css({\n" +
						"\t\"position\" : \"absolute\",\n" +
						"\t\"z-index\" :1,\n" +
						"\t\"left\"     : (dragDropOffset.left + window.pageXOffset),\n" +
						"\t\"top\"      : (dragDropOffset.top + window.pageYOffset),\n" +
						"\t\"width\"	: %3$d,\n" +
						"\t\"height\"	: %4$d,\n" +
						"});\n\n", getMarkupId(), canvas.getMarkupId(), (int) (desiredWidth * 1.5),
				(int) (height * 1.5 )));
	}
}
