package com.jeroensteenbeeke.topiroll.beholder.beans.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jeroensteenbeeke.topiroll.beholder.beans.IdentityService;
import com.jeroensteenbeeke.topiroll.beholder.dao.BeholderUserDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.BeholderUserFilter;

@Component
@Scope(value = "request")
class IdentityServiceImpl implements IdentityService {
	@Autowired
	private BeholderUserDAO userDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public BeholderUser getOrCreateUser(UserDescriptor descriptor) {
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
