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
package com.jeroensteenbeeke.topiroll.beholder.entities;

public enum VisibilityStatus {
	VISIBLE {
		@Override
		public boolean isVisible(boolean previewMode) {
			return true;
		}
	},
	DM_ONLY {
		@Override
		public boolean isVisible(boolean previewMode) {
			return previewMode;
		}
	},
	INVISIBLE {
		@Override
		public boolean isVisible(boolean previewMode) {
			return false;
		}
	};

	public abstract boolean isVisible(boolean previewMode);
}
