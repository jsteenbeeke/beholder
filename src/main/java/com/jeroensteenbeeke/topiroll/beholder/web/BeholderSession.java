package com.jeroensteenbeeke.topiroll.beholder.web;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;

import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;

public class BeholderSession extends WebSession {
	private static final long serialVersionUID = 1L;

	private String state;

	private IModel<BeholderUser> userModel = Model.of();

	public BeholderSession(Request request) {
		super(request);
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getState() {
		return state;
	}

	public static BeholderSession get() {
		return (BeholderSession) WebSession.get();
	}

	@CheckForNull
	public BeholderUser getUser() {
		return userModel.getObject();
	}

	public void setUser(@Nullable BeholderUser user) {
		if (user == null) {
			this.userModel = Model.of();
		}

		this.userModel = ModelMaker.wrap(user);
	}

	@Override
	public void detach() {
		super.detach();

		userModel.detach();
	}

}
