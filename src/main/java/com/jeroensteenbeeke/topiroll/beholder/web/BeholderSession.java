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
