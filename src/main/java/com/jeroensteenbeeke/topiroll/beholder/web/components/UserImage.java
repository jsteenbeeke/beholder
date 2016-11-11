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
		return getSrcSetModel().getObject().size() >= 1;
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

			return "";
		}
	}
}
