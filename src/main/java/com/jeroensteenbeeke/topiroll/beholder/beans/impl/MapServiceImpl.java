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

import com.jeroensteenbeeke.hyperion.util.ImageUtil;
import com.jeroensteenbeeke.hyperion.util.TypedActionResult;
import com.jeroensteenbeeke.topiroll.beholder.BeholderRegistry;
import com.jeroensteenbeeke.topiroll.beholder.BeholderRegistry.RegistryEntry;
import com.jeroensteenbeeke.topiroll.beholder.beans.AmazonS3Service;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.beans.URLService;
import com.jeroensteenbeeke.topiroll.beholder.dao.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.FogOfWarGroupVisibilityFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.FogOfWarShapeVisibilityFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.InitiativeParticipantFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.data.ClearMap;
import com.jeroensteenbeeke.topiroll.beholder.web.data.MapRenderable;
import com.jeroensteenbeeke.topiroll.beholder.web.data.UpdatePortraits;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.sound.sampled.Port;
import java.awt.*;
import java.io.*;
import java.sql.Blob;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@Scope(value = "request")
class MapServiceImpl implements MapService {
	private static final Logger log = LoggerFactory.getLogger(MapServiceImpl.class);

	@Autowired
	private ScaledMapDAO mapDAO;

	@Autowired
	private MapViewDAO viewDAO;

	@Autowired
	private FogOfWarShapeDAO shapeDAO;

	@Autowired
	private FogOfWarGroupDAO groupDAO;

	@Autowired
	private FogOfWarVisibilityDAO visibilityDAO;

	@Autowired
	private FogOfWarGroupVisibilityDAO groupVisibilityDAO;

	@Autowired
	private FogOfWarShapeVisibilityDAO shapeVisibilityDAO;

	@Autowired
	private TokenDefinitionDAO tokenDefinitionDAO;

	@Autowired
	private TokenInstanceDAO tokenInstanceDAO;

	@Autowired
	private URLService urlService;

	@Autowired
	private PortraitDAO portraitDAO;

	@Autowired
	private PortraitVisibilityDAO portraitVisibilityDAO;

	@Autowired
	private InitiativeParticipantDAO participantDAO;

	@Autowired
	private AmazonS3Service amazonS3Service;

	@Nonnull
	@Override
	@Transactional
	public TypedActionResult<ScaledMap> createMap(@Nonnull BeholderUser user,
												  @Nonnull String name, int squareSize, @Nonnull File data,
												  @Nullable MapFolder folder) {
		TypedActionResult<Dimension> dimResult = ImageUtil.getImageDimensions(data);
		if (!dimResult.isOk()) {
			return TypedActionResult.fail(dimResult);
		}

		Dimension dimension = dimResult.getObject();

		TypedActionResult<String> uploadResult;

		try {
			TypedActionResult<String> mimeType = ImageUtil.getMimeType
					(data);
			if (!mimeType.isOk()) {
				return TypedActionResult.fail(mimeType);
			}

			uploadResult =
					amazonS3Service
							.uploadImage(AmazonS3Service.ImageType.MAP, mimeType.getObject(), new
									FileInputStream(data), data.length());
		} catch (IOException e) {
			return TypedActionResult.fail("Could not open file for upload: %s", e.getMessage());
		}

		if (!uploadResult.isOk()) {
			return TypedActionResult.fail(uploadResult);
		}


		ScaledMap map = new ScaledMap();

		map.setAmazonKey(uploadResult.getObject());
		map.setName(name);
		map.setSquareSize(squareSize);
		map.setOwner(user);
		map.setBasicHeight((int) dimension.getHeight());
		map.setBasicWidth((int) dimension.getWidth());
		map.setFolder(folder);
		mapDAO.save(map);

		return TypedActionResult.ok(map);
	}

	@Override
	@Transactional
	public TokenInstance createTokenInstance(@Nonnull TokenDefinition token, @Nonnull ScaledMap map,
									@Nonnull TokenBorderType borderType, int x, int y, String badge) {
		TokenInstance instance = new TokenInstance();
		instance.setBadge(badge != null && !badge.isEmpty() ? badge : null);
		instance.setBorderType(borderType);
		instance.setDefinition(token);
		instance.setMap(map);
		instance.setOffsetX(x);
		instance.setOffsetY(y);
		instance.setShow(true);
		tokenInstanceDAO.save(instance);

		map.getTokens().add(instance);

		map.getSelectedBy().forEach(this::refreshView);

		return instance;
	}

	@Override
	@Transactional
	public void selectMap(@Nonnull MapView view, @Nonnull ScaledMap map) {
		view.setSelectedMap(map);
		viewDAO.update(view);

		refreshView(view);
	}

	@Override
	@Transactional
	public void unselectMap(@Nonnull MapView view) {
		view.setSelectedMap(null);
		viewDAO.update(view);

		refreshView(view);
	}

	@Override
	@Transactional
	public void delete(@Nonnull MapView view) {
		view.getVisibilities().forEach(visibilityDAO::delete);
		viewDAO.delete(view);

	}

	@Override
	@Transactional
	public void addFogOfWarCircle(@Nonnull ScaledMap map, int radius, int offsetX,
								  int offsetY) {
		FogOfWarCircle circle = new FogOfWarCircle();
		circle.setMap(map);
		circle.setOffsetX(offsetX);
		circle.setOffsetY(offsetY);
		circle.setRadius(radius);
		shapeDAO.save(circle);

	}

	@Override
	@Transactional
	public void addFogOfWarRect(@Nonnull ScaledMap map, int width, int height,
								int offsetX, int offsetY) {
		FogOfWarRect rect = new FogOfWarRect();
		rect.setMap(map);
		rect.setHeight(height);
		rect.setWidth(width);
		rect.setOffsetX(offsetX);
		rect.setOffsetY(offsetY);
		shapeDAO.save(rect);
	}

	@Override
	@Transactional
	public void addFogOfWarTriangle(@Nonnull ScaledMap map, int width, int height,
									int offsetX, int offsetY, @Nonnull TriangleOrientation orientation) {
		FogOfWarTriangle triangle = new FogOfWarTriangle();
		triangle.setMap(map);
		triangle.setVerticalSide(height);
		triangle.setHorizontalSide(width);
		triangle.setOffsetX(offsetX);
		triangle.setOffsetY(offsetY);
		triangle.setOrientation(orientation);
		shapeDAO.save(triangle);
	}

	@Override
	@Transactional
	public TypedActionResult<FogOfWarGroup> createGroup(@Nonnull ScaledMap map,
														@Nonnull String name, @Nonnull List<FogOfWarShape> shapes) {
		if (shapes.isEmpty()) {
			return TypedActionResult.fail("No shapes selected");
		}

		FogOfWarGroup group = new FogOfWarGroup();
		group.setMap(map);
		group.setName(name);
		groupDAO.save(group);

		shapes.forEach(shape -> {
			shape.setGroup(group);
			shapeDAO.update(shape);
		});

		return TypedActionResult.ok(group);
	}

	@Override
	@Transactional
	public TypedActionResult<FogOfWarGroup> editGroup(@Nonnull FogOfWarGroup group,
													  @Nonnull String name, @Nonnull List<FogOfWarShape> keep,
													  @Nonnull List<FogOfWarShape> remove) {
		if (keep.isEmpty()) {
			return TypedActionResult.fail("No shapes selected");
		}

		group.setName(name);
		groupDAO.update(group);

		keep.forEach(shape -> {
			shape.setGroup(group);
			shapeDAO.update(shape);
		});

		remove.forEach(shape -> {
			shape.setGroup(null);
			shapeDAO.update(shape);
		});

		return TypedActionResult.ok(group);
	}

	@Override
	@Transactional
	public void setGroupVisibility(@Nonnull MapView view, @Nonnull FogOfWarGroup group,
								   @Nonnull VisibilityStatus status) {
		FogOfWarGroupVisibilityFilter filter = new FogOfWarGroupVisibilityFilter();
		filter.view().set(view);
		filter.group().set(group);

		FogOfWarGroupVisibility visibility = groupVisibilityDAO
				.getUniqueByFilter(filter);

		if (visibility == null) {
			visibility = new FogOfWarGroupVisibility();
			visibility.setGroup(group);
			visibility.setView(view);
			visibility.setStatus(status);
			groupVisibilityDAO.save(visibility);
		} else {
			visibility.setStatus(status);
			groupVisibilityDAO.update(visibility);
		}

		refreshView(view);
	}

	@Override
	@Transactional
	public void setShapeVisibility(@Nonnull MapView view, @Nonnull FogOfWarShape shape,
								   @Nonnull VisibilityStatus status) {
		FogOfWarShapeVisibilityFilter filter = new FogOfWarShapeVisibilityFilter();
		filter.view().set(view);
		filter.shape().set(shape);

		FogOfWarShapeVisibility visibility = shapeVisibilityDAO
				.getUniqueByFilter(filter);

		if (visibility == null) {
			visibility = new FogOfWarShapeVisibility();
			visibility.setShape(shape);
			visibility.setView(view);
			visibility.setStatus(status);
			shapeVisibilityDAO.save(visibility);
		} else {
			visibility.setStatus(status);
			shapeVisibilityDAO.update(visibility);
		}

		refreshView(view);
	}

	@Override
	@Transactional
	public TypedActionResult<TokenDefinition> createToken(@Nonnull BeholderUser user, @Nonnull
			String name,
									   int diameter, @Nonnull byte[] image) {
		TypedActionResult<String> uploadResult =
				amazonS3Service.uploadImage(AmazonS3Service.ImageType.TOKEN,
						image);

		if (uploadResult.isOk()) {
			TokenDefinition def = new TokenDefinition();
			def.setOwner(user);
			def.setDiameterInSquares(diameter);
			def.setName(name);
			def.setAmazonKey(uploadResult.getObject());

			tokenDefinitionDAO.save(def);

			return TypedActionResult.ok(def);
		}

		return TypedActionResult.fail(uploadResult);
	}

	@Override
	@Transactional
	public TypedActionResult<Portrait> createPortrait(@Nonnull BeholderUser user, @Nonnull String
			name, @Nonnull byte[] image) {
		TypedActionResult<String> uploadResult =
				amazonS3Service.uploadImage(AmazonS3Service.ImageType.PORTRAIT, image);

		if (uploadResult.isOk()) {

			Portrait portrait = new Portrait();
			portrait.setOwner(user);
			portrait.setName(name);
			portrait.setAmazonKey(uploadResult.getObject());

			portraitDAO.save(portrait);

			return TypedActionResult.ok(portrait);
		}

		return TypedActionResult.fail(uploadResult);
	}

	@Override
	public void selectPortrait(@Nonnull MapView view, @Nonnull Portrait portrait,
							   @Nonnull PortraitVisibilityLocation location) {
		Set<PortraitVisibilityLocation> excludedLocations = location.getExcludedLocations();
		removeVisibilities(view, l -> l == location || excludedLocations.contains(l));

		PortraitVisibility visibility = new PortraitVisibility();
		visibility.setLocation(location);
		visibility.setPortrait(portrait);
		visibility.setView(view);

		portraitVisibilityDAO.save(visibility);

		view.getPortraitVisibilities().add(visibility);

		BeholderRegistry.instance.sendToView(view.getId(), s -> !s.isPreviewMode(), new UpdatePortraits(view));
	}

	private void removeVisibilities(@Nonnull MapView view, Predicate<PortraitVisibilityLocation> locationPredicate) {
		Set<PortraitVisibility> toDelete = new HashSet<>();

		for (PortraitVisibility visibility : view.getPortraitVisibilities()) {
			if (locationPredicate.test(visibility.getLocation())) {
				toDelete.add(visibility);
			}
		}

		toDelete.forEach(view.getPortraitVisibilities()::remove);
		toDelete.forEach(portraitVisibilityDAO::delete);
	}

	@Override
	public void unselectPortrait(@Nonnull MapView view, @Nonnull Portrait portrait,
								 @Nonnull PortraitVisibilityLocation location) {
		removeVisibilities(view, location::equals);
		BeholderRegistry.instance.sendToView(view.getId(), s -> !s.isPreviewMode(), new UpdatePortraits(view));
	}

	@Override
	@Transactional
	public void ungroup(@Nonnull FogOfWarGroup group) {
		ScaledMap map = group.getMap();

		group.getShapes().forEach(s -> {
			s.setGroup(null);
			shapeDAO.update(s);
		});

		group.getVisibilities().forEach(groupVisibilityDAO::delete);

		groupDAO.delete(group);

		map.getSelectedBy().forEach(this::refreshView);

	}

	@Override
	@Transactional
	public void deleteShape(@Nonnull FogOfWarShape shape) {
		ScaledMap map = shape.getMap();

		shape.getVisibilities().forEach(shapeVisibilityDAO::delete);

		shapeDAO.delete(shape);

		map.getSelectedBy().forEach(this::refreshView);
	}

	@Override
	@Transactional
	public void setTokenBorderType(@Nonnull TokenInstance instance,
								   @Nonnull TokenBorderType type) {
		TokenInstance inst = tokenInstanceDAO.load(instance.getId());

		inst.setBorderType(type);
		tokenInstanceDAO.update(inst);

		inst.getMap().getSelectedBy().forEach(this::refreshView);
	}

	@Override
	@Transactional
	public void setTokenHP(@Nonnull TokenInstance instance, Integer currentHP,
						   Integer maxHP) {
		TokenInstance inst = tokenInstanceDAO.load(instance.getId());

		inst.setCurrentHitpoints(currentHP);
		inst.setMaxHitpoints(maxHP);
		tokenInstanceDAO.update(inst);

		inst.getMap().getSelectedBy().forEach(this::refreshView);
	}

	@Override
	@Transactional
	public void showToken(@Nonnull TokenInstance instance) {
		instance.setShow(true);
		tokenInstanceDAO.update(instance);

		instance.getMap().getSelectedBy().forEach(this::refreshView);
	}

	@Override
	@Transactional
	public void hideToken(@Nonnull TokenInstance instance) {
		instance.setShow(false);
		tokenInstanceDAO.update(instance);

		instance.getMap().getSelectedBy().forEach(this::refreshView);
	}

	@Override
	@Transactional
	public void setTokenNote(@Nonnull TokenInstance instance, String note) {
		instance.setNote(note);
		tokenInstanceDAO.update(instance);
	}

	@Override
	@Transactional
	public void updateTokenLocation(@Nonnull TokenInstance instance, int x, int y) {
		instance.setOffsetX(x);
		instance.setOffsetY(y);
		tokenInstanceDAO.update(instance);

		instance.getMap().getSelectedBy().forEach(this::refreshView);
	}

	@Override
	@Transactional
	public void refreshView(@Nonnull MapView view) {
		internalUpdateView(view, s -> true);
	}

	private void internalUpdateView(MapView view,
									Predicate<RegistryEntry> selector) {
		ScaledMap selectedMap = view.getSelectedMap();
		if (selectedMap == null) {
			BeholderRegistry.instance.sendToView(view.getId(), selector,
					new ClearMap());
		} else {
			updatePreview(view, selector, selectedMap);
			updateMainView(view, selector, selectedMap);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void initializeView(long viewId, @Nonnull String sessionId,
							   boolean previewMode) {
		MapView view = viewDAO.load(viewId);
		if (view != null) {
			internalUpdateView(view, e -> e.getSessionId().equals(sessionId)
					&& Boolean.compare(e.isPreviewMode(), previewMode) == 0);
			BeholderRegistry.instance
					.sendToView(view.getId(),
							e -> e.getSessionId().equals(sessionId)
									&& !e.isPreviewMode(),
							view.getInitiativeJS());
			BeholderRegistry.instance.sendToView(view.getId(), s -> !s.isPreviewMode(), new UpdatePortraits(view));

		}
	}

	@Override
	@Transactional
	public void gatherPlayerTokens(@Nonnull MapView view, int x, int y) {
		InitiativeParticipantFilter filter = new InitiativeParticipantFilter();
		filter.player(true);
		filter.view(view);

		List<InitiativeParticipant> participants = participantDAO.findByFilter(filter);

		double angle = Math.toRadians(360.0 / (double) participants.size());

		int distance = Optional.ofNullable(view.getSelectedMap())
				.map(ScaledMap::getSquareSize)
				.map(s -> s * participants.size())
				.orElse(view.getHeight()/10);

		double nextAngle = 0.0;

		int i = 0;

		for (InitiativeParticipant participant: participants) {
			double ax = Math.cos(nextAngle);
			double ay = Math.sin(nextAngle);

			double dx = ax * distance;
			double dy = ay * distance;

			log.info("P{}: θ{} | X{} Y{}", i++, Math.toDegrees(nextAngle), dx, dy);

			double nx = x + dx;
			double ny = y + dy;

			participant.setOffsetX((int) Math.round(nx));
			participant.setOffsetY((int) Math.round(ny));

			participantDAO.update(participant);

			nextAngle += angle;
		}
	}

	private void updateMainView(MapView view, Predicate<RegistryEntry> selector,
								ScaledMap map) {
		Dimension dimensions = map.getDisplayDimension(view);

		internalUpdateView(dimensions, false,
				selector.and(e -> !e.isPreviewMode()), view, map);
	}

	private void updatePreview(MapView view, Predicate<RegistryEntry> selector,
							   ScaledMap map) {
		Dimension dimensions = view.getPreviewDimensions();

		internalUpdateView(dimensions, true,
				selector.and(RegistryEntry::isPreviewMode), view, map);

	}

	private void internalUpdateView(Dimension dimensions, boolean previewMode,
									Predicate<RegistryEntry> selector, MapView view,
									ScaledMap map) {
		double factor = previewMode ? map.getPreviewFactor() : map.getDisplayFactor(view);

		MapRenderable renderable = mapToJS(dimensions, previewMode, view,
				map, factor);

		BeholderRegistry.instance.sendToView(view.getId(), selector,
				renderable);
	}

	private MapRenderable mapToJS(Dimension dimensions, boolean previewMode,
								  MapView view, ScaledMap map, double factor) {
		MapRenderable renderable = new MapRenderable();
		renderable.setSrc(map.getImageUrl());
		renderable.setWidth((int) dimensions.getWidth());
		renderable.setHeight((int) dimensions.getHeight());

		renderable.setTokens(map.getTokens().stream()
				.filter(TokenInstance::isShow)
				.filter(t -> t.isVisible(view, previewMode))
				.map(t -> t.toJS(factor)).collect(Collectors.toList()));
		if (previewMode) {
			renderable.getTokens().forEach(t -> t.setLabel(null));
		}
		renderable.setAreaMarkers(view.getMarkers().stream()
				.map(a -> a.toJS(factor)).collect(Collectors.toList()));
		renderable.setRevealed(map.getFogOfWarShapes().stream()
				.filter(s -> s.shouldRender(view, previewMode))
				.map(s -> s.toJS(factor)).collect(Collectors.toList()));
		return renderable;
	}
}
