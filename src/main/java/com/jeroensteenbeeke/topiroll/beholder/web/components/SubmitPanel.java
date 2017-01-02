/**
 * This file is part of Beholder
 * (C) 2016 Jeroen Steenbeeke
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

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.danekja.java.util.function.serializable.SerializableConsumer;

import com.googlecode.wicket.jquery.ui.markup.html.link.SubmitLink;

public class SubmitPanel<T> extends Panel {
	private static final long serialVersionUID = 1L;

	public SubmitPanel(String id, Form<T> form, SerializableConsumer<T> onAfterSubmit) {
		super(id);

		SubmitLink submitLink = new SubmitLink("submit", form) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onAfterSubmit() {
				super.onAfterSubmit();
				
				onAfterSubmit.accept(form.getModelObject());
			}
		};
		
		decorateLink(submitLink);
		
		add(submitLink);
	}

	protected void decorateLink(SubmitLink submitLink) {
		
	}
	
	

}
