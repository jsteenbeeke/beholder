package com.jeroensteenbeeke.topiroll.beholder.web.components;

import java.io.Serializable;
import java.util.List;

import org.apache.wicket.markup.html.image.ExternalImage;
import org.apache.wicket.model.LoadableDetachableModel;

import com.google.common.collect.ImmutableList;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.web.BeholderSession;

public class UserImage extends ExternalImage {
	private static final long serialVersionUID = 1L;

	public UserImage(String id) {
		super(id);
		setSrcSetModel(new UserImageModel());
	}
	
	@Override
	public boolean isVisible() {
		return getSrcSetModel().getObject().size() >= 1;
	}

	private static final class UserImageModel
			extends LoadableDetachableModel<List<Serializable>> {

		private static final long serialVersionUID = 1L;

		@Override
		protected List<Serializable> load() {
			BeholderUser user = BeholderSession.get().getUser();

			if (user != null) {
				String picture = user.getAvatar();
				if (picture != null) {
					return ImmutableList.of(picture);
				}

			}

			return ImmutableList.of();
		}
	}
}
