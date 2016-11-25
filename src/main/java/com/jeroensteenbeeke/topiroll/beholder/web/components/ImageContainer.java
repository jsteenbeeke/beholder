package com.jeroensteenbeeke.topiroll.beholder.web.components;

import java.awt.Dimension;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ResourceReference;

public class ImageContainer extends WebMarkupContainer {
	private static final long serialVersionUID = 1L;

	public ImageContainer(String id, ResourceReference reference,
			Dimension dimension) {
		super(id);
		
		final int width = (int) dimension.getWidth();
		final int height = (int) dimension.getHeight();

		add(AttributeModifier.replace("style",
				new LoadableDetachableModel<String>() {
					private static final long serialVersionUID = 1L;

					@Override
					protected String load() {
						return String.format(
								"width: %dpx; height: %dpx; background-image: url('%s')",
								width, height, RequestCycle.get().urlFor(reference,
										new PageParameters()));
					}

				}));
	}

}
