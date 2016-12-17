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

import java.io.Serializable;

import org.apache.wicket.markup.html.image.ExternalImage;
import org.apache.wicket.model.LoadableDetachableModel;

import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.web.BeholderSession;

public class UserImage extends ExternalImage {
	private static final long serialVersionUID = 1L;

	public UserImage(String id) {
		super(id);
		UserImageModel model = new UserImageModel();
		setDefaultModel(model);

	}

	@Override
	public boolean isVisible() {
		return getDefaultModelObject() != null;
	}

	private static final class UserImageModel
			extends LoadableDetachableModel<Serializable> {

		private static final long serialVersionUID = 1L;

		@Override
		protected Serializable load() {
			BeholderUser user = BeholderSession.get().getUser();

			if (user != null) {
				String picture = user.getAvatar();
				if (picture != null) {
					return picture;
				}

			}

			return null;
		}
	}
}
