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

import com.jeroensteenbeeke.hyperion.heinlein.web.components.BootstrapFeedbackPanel;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.BootstrapModalWindow;
import org.apache.wicket.model.IModel;

public class DMModalWindow<T> extends BootstrapModalWindow {

	private static final long serialVersionUID = 3873220497150255788L;

	protected DMModalWindow(String id, String title) {
		super(id, title);
		setOutputMarkupPlaceholderTag(true);
		add(new BootstrapFeedbackPanel("feedback"));

		getDialog().setMarkupId("combat-modal");
	}

	protected DMModalWindow(String id, IModel<T> model, String title) {
		super(id, model, title);
		setOutputMarkupPlaceholderTag(true);
		add(new BootstrapFeedbackPanel("feedback"));

		getDialog().setMarkupId("combat-modal");
	}

	@SuppressWarnings("unchecked")
	public T getModelObject() {
		return (T) getDefaultModelObject();
	}

	public void setModelObject(T object) {
		setDefaultModelObject(object);
	}

	@SuppressWarnings("unchecked")
	public IModel<T> getModel() {
		return (IModel<T>) getDefaultModel();
	}

	public void setModel(IModel<T> model) {
		setDefaultModel(model);
	}

	public static class CannotCreateModalWindowException extends RuntimeException {

		private static final long serialVersionUID = 4852500151933563519L;


	}
}
