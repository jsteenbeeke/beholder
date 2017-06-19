/**
 * This file is part of Beholder
 * (C) 2016 Jeroen Steenbeeke
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jeroensteenbeeke.topiroll.beholder.beans.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jeroensteenbeeke.hyperion.solstice.api.Any;
import com.jeroensteenbeeke.topiroll.beholder.beans.IAccountInitializer;
import com.jeroensteenbeeke.topiroll.beholder.beans.IdentityService;
import com.jeroensteenbeeke.topiroll.beholder.dao.BeholderUserDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.BeholderUserFilter;

import javax.annotation.Nonnull;
import java.util.List;

@Component
@Scope(value = "request")
class IdentityServiceImpl implements IdentityService {
	@Autowired
	private BeholderUserDAO userDAO;

	@Autowired(required = false)
	private List<IAccountInitializer> initializers;

	@Nonnull
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public BeholderUser getOrCreateUser(@Nonnull UserDescriptor descriptor) {
		BeholderUserFilter filter = new BeholderUserFilter();
		filter.userId().set(descriptor.getUserId());

		BeholderUser user = userDAO.getUniqueByFilter(filter);

		if (user == null) {
			user = new BeholderUser();
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
		} else {
			user.setAccessToken(descriptor.getAccessToken());
			user.setAvatar(descriptor.getAvatar());
			user.setTeamId(descriptor.getTeamId());
			user.setUsername(descriptor.getUserName());
			userDAO.update(user);
		}

		return user;
	}
}
