/**
 * This file is part of Beholder
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
		this(id, RequestCycle.get()
				.urlFor(reference, new PageParameters())
				.toString(), dimension);
		
	}
	
	public ImageContainer(String id, final String url, Dimension dimension) {
		super(id);
		final int width = (int) dimension.getWidth();
		final int height = (int) dimension.getHeight();

		add(AttributeModifier.replace("style",
				new LoadableDetachableModel<String>() {
					private static final long serialVersionUID = 1L;

					@Override
					protected String load() {
						String target = url;
						if (target.contains("?")) {
							target = target + "&antiCache="
									+ System.currentTimeMillis();
						} else {
							target = target + "?antiCache="
									+ System.currentTimeMillis();
						}

						return String.format(
								"width: %dpx; height: %dpx; background-image: url('%s')",
								width, height, target);
					}

				}));
	}

}
