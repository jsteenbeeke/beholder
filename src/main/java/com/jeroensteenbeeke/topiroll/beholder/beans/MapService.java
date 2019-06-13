/**
 * This file is part of Beholder
 * (C) 2016-2019 Jeroen Steenbeeke
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
package com.jeroensteenbeeke.topiroll.beholder.beans;

import java.io.File;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.Null;

import com.jeroensteenbeeke.lux.TypedResult;
import com.jeroensteenbeeke.topiroll.beholder.entities.*;

public interface MapService {
	@Nonnull
	TypedResult<ScaledMap> createMap(@Nonnull BeholderUser user, @Nullable Campaign campaign, @Nonnull String name,
									 int squareSize, @Nonnull File data, @Nullable MapFolder folder);

	void selectMap(@Nonnull MapView view, @Nonnull ScaledMap map);

	void delete(@Nonnull MapView view);

	void refreshView(@Nonnull MapView view);

	void initializeView(long viewId, @Nonnull String sessionId, boolean previewMode);

	void selectMapAndSetFocus(@Nonnull MapView view, @Nonnull FogOfWarGroup group);

	//region Tokens

	TypedResult<TokenDefinition> createToken(@Nonnull BeholderUser user, @Nullable Campaign campaign, @Nonnull String name,
											 int diameter, @Nonnull byte[] image);


	TokenInstance createTokenInstance(@Nonnull TokenDefinition token, @Nonnull ScaledMap map,
									  @Nonnull TokenBorderType borderType, int x, int y, @Nullable String badge);

	void setTokenBorderType(@Nonnull TokenInstance instance, @Nonnull TokenBorderType type);

	void showToken(@Nonnull TokenInstance instance);

	void hideToken(@Nonnull TokenInstance instance);

	void setTokenHP(@Nonnull TokenInstance instance, @Nullable Integer currentHP, @Nullable Integer maxHP);

	void setTokenNote(@Nonnull TokenInstance instance, @Nullable String note);

	void updateTokenLocation(@Nonnull TokenInstance instance, int x, int y);
	//endregion

	//region FogOfWar
	void addFogOfWarCircle(@Nonnull ScaledMap map, int radius, int offsetX, int offsetY);

	void addFogOfWarRect(@Nonnull ScaledMap map, int width, int height, int offsetX,
						 int offsetY);

	TypedResult<FogOfWarGroup> createGroup(@Nonnull ScaledMap map,
										   @Nonnull String name, @Nonnull List<FogOfWarShape> shapes);

	void setGroupVisibility(@Nonnull MapView view, @Nonnull FogOfWarGroup group,
							@Nonnull VisibilityStatus status);

	void setShapeVisibility(@Nonnull MapView view, @Nonnull FogOfWarShape shape,
							@Nonnull VisibilityStatus status);

	void ungroup(@Nonnull FogOfWarGroup group);

	TypedResult<FogOfWarGroup> editGroup(@Nonnull FogOfWarGroup group,
										 @Nonnull String name, @Nonnull List<FogOfWarShape> keep,
										 @Nonnull List<FogOfWarShape> remove);

	void addFogOfWarTriangle(@Nonnull ScaledMap map, int width, int height,
							 int offsetX, int offsetY, @Nonnull TriangleOrientation orientation);

	void deleteShape(@Nonnull FogOfWarShape shape);

	//endregion

	//region Portraits
	TypedResult<Portrait> createPortrait(@Nonnull BeholderUser user, @Nullable Campaign campaign, @Nonnull String name,
										 @Nonnull byte[] image);

	void selectPortrait(@Nonnull MapView view, @Nonnull Portrait portrait, @Nonnull PortraitVisibilityLocation location);

	void unselectPortrait(@Nonnull MapView view, @Nonnull Portrait portrait, @Nonnull PortraitVisibilityLocation location);

	void gatherPlayerTokens(@Nonnull MapView view, int x, int y);


	//endregion

	@Nonnull
	MapLink createLink(@Nonnull FogOfWarGroup source, @Nonnull FogOfWarGroup target);

	void focusOnGroup(@Nonnull MapView view, @Nonnull FogOfWarGroup target);

	TypedResult<Integer> doorbell(String username);
}
