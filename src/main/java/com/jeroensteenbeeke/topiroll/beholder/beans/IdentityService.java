/**
 * This file is part of Beholder
 * (C) 2016 Jeroen Steenbeeke
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

import javax.annotation.Nonnull;

import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;

public interface IdentityService {
	public static class UserDescriptor {
		private String userId;

		private String teamId;

		private String userName;

		private String teamName;

		private String avatar;

		private String accessToken;

		public String getUserId() {
			return userId;
		}

		public UserDescriptor setUserId(String userId) {
			this.userId = userId;
			return this;
		}

		public String getTeamId() {
			return teamId;
		}

		public UserDescriptor setTeamId(String teamId) {
			this.teamId = teamId;
			return this;
		}

		public String getUserName() {
			return userName;
		}

		public UserDescriptor setUserName(String userName) {
			this.userName = userName;
			return this;
		}

		public String getTeamName() {
			return teamName;
		}

		public UserDescriptor setTeamName(String teamName) {
			this.teamName = teamName;
			return this;
		}

		public String getAvatar() {
			return avatar;
		}

		public UserDescriptor setAvatar(String avatar) {
			this.avatar = avatar;
			return this;
		}

		public String getAccessToken() {
			return accessToken;
		}

		public UserDescriptor setAccessToken(String accessToken) {
			this.accessToken = accessToken;
			return this;
		}

	}

	@Nonnull
	BeholderUser getOrCreateUser(@Nonnull UserDescriptor descriptor);
}
