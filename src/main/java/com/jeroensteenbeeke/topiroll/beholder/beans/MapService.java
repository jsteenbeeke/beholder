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
package com.jeroensteenbeeke.topiroll.beholder.beans;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.jeroensteenbeeke.hyperion.util.TypedActionResult;
import com.jeroensteenbeeke.topiroll.beholder.entities.*;

public interface MapService {
	@Nonnull
	TypedActionResult<ScaledMap> createMap(@Nonnull BeholderUser user, @Nonnull String name,
			int squareSize, byte[] data);

	void selectMap(@Nonnull MapView view, @Nonnull ScaledMap map);

	void unselectMap(@Nonnull MapView view);
	
	void delete(@Nonnull MapView view);

	void addFogOfWarCircle(ScaledMap map, int radius, int offsetX, int offsetY);

	void addFogOfWarRect(ScaledMap map, int width, int height, int offsetX,
			int offsetY);

	TypedActionResult<FogOfWarGroup> createGroup(@Nonnull ScaledMap map,
			@Nonnull String name, @Nonnull List<FogOfWarShape> shapes);

	void setGroupVisibility(@Nonnull MapView view, @Nonnull FogOfWarGroup group,
			@Nonnull VisibilityStatus status);

	void setShapeVisibility(@Nonnull MapView view, @Nonnull FogOfWarShape shape,
			@Nonnull VisibilityStatus status);

	TokenDefinition createToken(@Nonnull BeholderUser user, @Nonnull String name,
			@Nonnull int diameter, @Nonnull byte[] image);

	void ungroup(@Nonnull FogOfWarGroup group);

	TypedActionResult<FogOfWarGroup> editGroup(@Nonnull FogOfWarGroup group,
			@Nonnull String name, @Nonnull List<FogOfWarShape> keep,
			@Nonnull List<FogOfWarShape> remove);

	void addFogOfWarTriangle(ScaledMap map, int width, int height,
			int offsetX, int offsetY, TriangleOrientation orientation);

	void deleteShape(@Nonnull FogOfWarShape shape);

	void createTokenInstance(@Nonnull  TokenDefinition token, @Nonnull ScaledMap map,
			@Nonnull TokenBorderType borderType, int x, int y, @Nullable String badge);

	void setTokenBorderType(@Nonnull TokenInstance instance, @Nonnull TokenBorderType type);
	
	void showToken(@Nonnull TokenInstance instance);
	
	void hideToken(@Nonnull TokenInstance instance);
	
	void setTokenHP(@Nonnull TokenInstance instance, @Nullable Integer currentHP, @Nullable Integer maxHP);
}
