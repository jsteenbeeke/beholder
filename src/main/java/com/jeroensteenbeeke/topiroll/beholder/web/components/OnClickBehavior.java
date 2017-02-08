package com.jeroensteenbeeke.topiroll.beholder.web.components;

import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.CallbackParameter;

import com.googlecode.wicket.jquery.core.IJQueryWidget.JQueryWidget;
import com.googlecode.wicket.jquery.core.JQueryEvent;
import com.googlecode.wicket.jquery.core.ajax.IJQueryAjaxAware;
import com.googlecode.wicket.jquery.core.ajax.JQueryAjaxBehavior;
import com.googlecode.wicket.jquery.core.utils.RequestCycleUtils;
import com.googlecode.wicket.jquery.ui.JQueryUIBehavior;

public class OnClickBehavior extends JQueryUIBehavior
		implements IJQueryAjaxAware {

	private static final long serialVersionUID = 1L;

	private Component component;

	private OnClickAjaxBehavior onClickBehavior;

	public OnClickBehavior() {
		super(null, "click");
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

		this.onClickBehavior = new OnClickAjaxBehavior(this);
		this.component.add(this.onClickBehavior);

	}

	@Override
	protected String $() {
		return String.format("jQuery('%s').on('click', %s);", getSelector(), this.onClickBehavior.getCallbackFunction());
	}

	public void onAjax(AjaxRequestTarget target, JQueryEvent event) {
		if (event instanceof ClickEvent) {
			this.onClick(target, (ClickEvent) event);
		}
	}

	protected void onClick(AjaxRequestTarget target, ClickEvent event) {

	}

	public static class OnClickAjaxBehavior extends JQueryAjaxBehavior {

		private static final long serialVersionUID = 1L;

		public OnClickAjaxBehavior(IJQueryAjaxAware source) {
			super(source);
		}

		@Override
		protected JQueryEvent newEvent() {
			return new ClickEvent();
		}

		@Override
		protected CallbackParameter[] getCallbackParameters() {
			return new CallbackParameter[] { CallbackParameter.context("event"), // lf
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
							"event.offsetY | 0"), // cast
													// to
													// int,
													// no
													// rounding
					CallbackParameter.resolved("offsetLeft",
							"event.offsetX | 0") // cast
													// to
													// int,
													// no
													// rounding
			};
		}
	}

	public static class ClickEvent extends JQueryEvent {
		private int top;

		private int left;

		private int offsetTop;

		private int offsetLeft;

		private ClickEvent() {
			this.top = RequestCycleUtils.getQueryParameterValue("top")
					.toInt(-1);
			this.left = RequestCycleUtils.getQueryParameterValue("left")
					.toInt(-1);
			this.offsetTop = RequestCycleUtils
					.getQueryParameterValue("offsetTop").toInt(-1);
			this.offsetLeft = RequestCycleUtils
					.getQueryParameterValue("offsetLeft").toInt(-1);
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
	}

}
