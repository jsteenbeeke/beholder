package com.jeroensteenbeeke.topiroll.beholder.web.components;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;

public class ImageCanvas extends WebComponent {
	private static final long serialVersionUID = 1L;

	private IResource imageResource;

	public ImageCanvas(String id, IResource imageResource) {
		super(id);
		this.imageResource = imageResource;
	}

	@Override
	protected void onComponentTag(ComponentTag tag) {
		checkComponentTag(tag, "canvas");

	}

	@Override
	public void renderHead(IHeaderResponse response) {

		super.renderHead(response);

		StringBuilder builder = new StringBuilder();
		builder.append("var canvas = document.getElementById('")
				.append(getMarkupId()).append("');\n");

		builder.append("var context = canvas.getContext('2d');");
		builder.append("var imageObj = new Image();\n");

		builder.append("imageObj.onload = function() {\n");
		builder.append("\tcontext.drawImage(imageObj, 0, 0);\n");
		builder.append("};\n");
		builder.append("imageObj.src = '")
				.append(RequestCycle.get().urlFor(new ResourceReference(
						ImageCanvas.class, "img-".concat(getMarkupId())) {
					private static final long serialVersionUID = 1L;

					@Override
					public IResource getResource() {
						return imageResource;
					}
				}, new PageParameters())).append("';\n");

		response.render(OnDomReadyHeaderItem.forScript(builder.toString()));
	}
}
