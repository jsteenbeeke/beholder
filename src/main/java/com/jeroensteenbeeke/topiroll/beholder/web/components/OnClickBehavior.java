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
/**
 * This file is part of Beholder (C) 2016 Jeroen Steenbeeke
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.jeroensteenbeeke.topiroll.beholder.web.components;

import com.googlecode.wicket.jquery.core.IJQueryWidget.JQueryWidget;
import com.googlecode.wicket.jquery.core.JQueryEvent;
import com.googlecode.wicket.jquery.core.ajax.IJQueryAjaxAware;
import com.googlecode.wicket.jquery.core.ajax.JQueryAjaxBehavior;
import com.googlecode.wicket.jquery.core.utils.RequestCycleUtils;
import com.googlecode.wicket.jquery.ui.JQueryUIBehavior;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.CallbackParameter;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.resource.JQueryPluginResourceReference;

import java.io.Serializable;

public abstract class OnClickBehavior extends JQueryUIBehavior
		implements IJQueryAjaxAware {

	private static final long serialVersionUID = 1L;

	private Component component;

	private OnClickAjaxBehavior onClickBehavior;

	private boolean stopPropagation = false;

	public OnClickBehavior() {
		super(null, "click");

	}

	public OnClickBehavior withoutPropagation() {
		stopPropagation = true;
		return this;
	}

	@Override
	public void bind(Component component) {
		super.bind(component);

		if (this.component != null) {
			throw new WicketRuntimeException(
					"Behavior is already bound to another component.");
		}

		this.component = component; // warning, not thread-safe: the instance of
		// this behavior should only be used once

		if (this.selector == null) {
			this.selector = JQueryWidget.getSelector(this.component);
		}

		this.onClickBehavior = new OnClickAjaxBehavior(this, stopPropagation);
		this.component.add(this.onClickBehavior);

	}

	@Override
	protected String $() {
		return String.format("jQuery('%s').on('click', %s);", getSelector(),
				this.onClickBehavior.getCallbackFunction());
	}

	public void onAjax(AjaxRequestTarget target, JQueryEvent event) {
		if (event instanceof ClickEvent) {
			this.onClick(target, (ClickEvent) event);
		}
	}

	@Override
	public void renderHead(Component component, IHeaderResponse response) {
		super.renderHead(component, response);

		response.render(JavaScriptHeaderItem
				.forReference(new JQueryPluginResourceReference(OnClickBehavior.class,
						"js/click.js")));
	}

	protected abstract void onClick(AjaxRequestTarget target, ClickEvent event);

	public static class OnClickAjaxBehavior extends JQueryAjaxBehavior {

		private static final long serialVersionUID = 1L;

		private final boolean stopPropagation;

		public OnClickAjaxBehavior(IJQueryAjaxAware source, boolean stopPropagation) {
			super(source);
			this.stopPropagation = stopPropagation;
		}

		@Override
		protected JQueryEvent newEvent() {
			return new ClickEvent();
		}

		@Override
		public CharSequence getCallbackFunctionBody(CallbackParameter... extraParameters) {
			CharSequence script = super.getCallbackFunctionBody(extraParameters);

			if (stopPropagation) {
				script =  "event.stopPropagation();\n" + script;
			}

			return script;
		}

		@Override
		protected CallbackParameter[] getCallbackParameters() {
			return new CallbackParameter[]{CallbackParameter.context("event"), // lf
					CallbackParameter.context("ui"), // lf
					CallbackParameter.resolved("top", "event.pageY | 0"), // cast
					// to
					// int,
					// no
					// rounding
					CallbackParameter.resolved("left", "event.pageX | 0"), // cast
					// to
					// int,
					// no
					// rounding
					CallbackParameter.resolved("offsetTop",
							String.format("resolveOffsetY('%s', event) | 0",
									getComponent().getMarkupId())), // cast
					// to
					// int,
					// no
					// rounding
					CallbackParameter.resolved("offsetLeft",
							String.format("resolveOffsetX('%s', event) | 0",
									getComponent().getMarkupId())) // cast
					// to
					// int,
					// no
					// rounding
			};
		}
	}

	public static class ClickEvent extends JQueryEvent implements Serializable {

		private static final long serialVersionUID = 8377447935056799904L;

		private int top;

		private int left;

		private int offsetTop;

		private int offsetLeft;

		private long timeMarker;

		private ClickEvent() {
			this.top = RequestCycleUtils.getQueryParameterValue("top")
					.toInt(-1);
			this.left = RequestCycleUtils.getQueryParameterValue("left")
					.toInt(-1);
			this.offsetTop = RequestCycleUtils
					.getQueryParameterValue("offsetTop").toInt(-1);
			this.offsetLeft = RequestCycleUtils
					.getQueryParameterValue("offsetLeft").toInt(-1);
			this.timeMarker = System.currentTimeMillis() / 5000;
		}

		public int getLeft() {
			return left;
		}

		public int getTop() {
			return top;
		}

		public int getOffsetLeft() {
			return offsetLeft;
		}

		public int getOffsetTop() {
			return offsetTop;
		}

		public long getTimeMarker() {
			return timeMarker;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			ClickEvent that = (ClickEvent) o;

			if (top != that.top) return false;
			if (left != that.left) return false;
			if (offsetTop != that.offsetTop) return false;
			if (offsetLeft != that.offsetLeft) return false;
			return timeMarker == that.timeMarker;
		}

		@Override
		public int hashCode() {
			int result = top;
			result = 31 * result + left;
			result = 31 * result + offsetTop;
			result = 31 * result + offsetLeft;
			result = 31 * result + (int) (timeMarker ^ (timeMarker >>> 32));
			return result;
		}
	}

}
