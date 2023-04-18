/*
 * This file is part of Beholder
 * Copyright (C) 2016 - 2023 Jeroen Steenbeeke
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
package com.jeroensteenbeeke.topiroll.beholder.beans;

import com.jeroensteenbeeke.topiroll.beholder.beans.data.UserDescriptor;
import com.jeroensteenbeeke.topiroll.beholder.dao.BeholderUserDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.BeholderUserFilter;
import io.vavr.control.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import org.jetbrains.annotations.NotNull;
import java.util.List;

@Service
@Scope(value = "request")
public class IdentityService {
	@Autowired
	private BeholderUserDAO userDAO;

	@Autowired(required = false)
	private List<IAccountInitializer> initializers;

	@NotNull
	@Transactional(propagation = Propagation.REQUIRED)
	public BeholderUser getOrCreateUser(@NotNull UserDescriptor descriptor) {
		BeholderUserFilter filter = new BeholderUserFilter();
		filter.userId().set(descriptor.getUserId());

		Option<BeholderUser> userOption = userDAO.getUniqueByFilter(filter);

		if (userOption.isEmpty()) {
			BeholderUser user = new BeholderUser();
			user.setAccessToken(descriptor.getAccessToken());
			user.setAvatar(descriptor.getAvatar());
			user.setTeamId(descriptor.getTeamId());
			user.setUserId(descriptor.getUserId());
			user.setUsername(descriptor.getUserName());
			userDAO.save(user);
			if (initializers != null) {
				for (IAccountInitializer initializer : initializers) {
					initializer.onAccountCreated(user);
				}
			}

			return user;
		} else {
			BeholderUser user = userOption.get();

			user.setAccessToken(descriptor.getAccessToken());
			user.setAvatar(descriptor.getAvatar());
			user.setTeamId(descriptor.getTeamId());
			user.setUsername(descriptor.getUserName());
			userDAO.update(user);

			return user;
		}
	}
}
