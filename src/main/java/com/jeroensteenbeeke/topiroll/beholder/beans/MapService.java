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

import com.jeroensteenbeeke.hyperion.util.ImageUtil;
import com.jeroensteenbeeke.lux.TypedResult;
import com.jeroensteenbeeke.topiroll.beholder.BeholderRegistry;
import com.jeroensteenbeeke.topiroll.beholder.BeholderRegistry.RegistryEntry;
import com.jeroensteenbeeke.topiroll.beholder.beans.data.ImageType;
import com.jeroensteenbeeke.topiroll.beholder.dao.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.*;
import com.jeroensteenbeeke.topiroll.beholder.web.data.*;
import com.jeroensteenbeeke.topiroll.beholder.web.data.visitors.AreaMarkerShapeVisitor;
import com.jeroensteenbeeke.topiroll.beholder.web.data.visitors.FogOfWarShapeToJSShapeVisitor;
import com.jeroensteenbeeke.topiroll.beholder.web.data.visitors.FogOfWarShapeXCoordinateVisitor;
import com.jeroensteenbeeke.topiroll.beholder.web.data.visitors.FogOfWarShapeYCoordinateVisitor;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Scope(value = "request")
public class MapService {
	private static final Logger log = LoggerFactory
			.getLogger(MapService.class);

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
	private RemoteImageService remoteImageService;

	@Autowired
	private MapLinkDAO mapLinkDAO;

	@NotNull
	@Transactional
	public TypedResult<ScaledMap> createMap(@NotNull BeholderUser user,
											@Nullable Campaign campaign, @NotNull String name, int squareSize,
											@NotNull File data, @Nullable MapFolder folder) {
		TypedResult<Dimension> dimResult = ImageUtil.getImageDimensions(data);
		if (!dimResult.isOk()) {
			return dimResult.map(d -> null);
		}

		Dimension dimension = dimResult.getObject();

		TypedResult<String> uploadResult;

		try {
			TypedResult<String> mimeType = ImageUtil.getMimeType(data);
			if (!mimeType.isOk()) {
				return mimeType.map(s -> null);
			}

			uploadResult = remoteImageService
					.uploadImage(ImageType.MAP,
								 mimeType.getObject(), new FileInputStream(data),
								 data.length());
		} catch (IOException e) {
			return TypedResult
					.fail("Could not open file for upload: %s", e.getMessage());
		}

		if (!uploadResult.isOk()) {
			return uploadResult.map(s -> null);
		}

		ScaledMap map = new ScaledMap();

		map.setAmazonKey(uploadResult.getObject());
		map.setName(name);
		map.setSquareSize(squareSize);
		map.setOwner(user);
		map.setBasicHeight((int) dimension.getHeight());
		map.setBasicWidth((int) dimension.getWidth());
		map.setFolder(folder);

		map.setCampaign(
				Option.of(folder).flatMap(f -> Option.of(f.getCampaign()))
					  .getOrElse(campaign));

		mapDAO.save(map);

		return TypedResult.ok(map);
	}

	@Transactional
	public TokenInstance createTokenInstance(@NotNull TokenDefinition token,
											 @NotNull ScaledMap map, @NotNull TokenBorderType borderType, int x,
											 int y, String badge) {
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

	@Transactional
	public void selectMap(@NotNull MapView view, @NotNull ScaledMap map) {

		view.setSelectedMap(map);
		viewDAO.update(view);
		viewDAO.flush();

		refreshView(viewDAO.load(view.getId())
						   .getOrElseThrow(IllegalStateException::new));
	}

	@Transactional
	public void delete(@NotNull MapView view) {
		view.getVisibilities().forEach(visibilityDAO::delete);
		viewDAO.delete(view);

	}

	@Transactional
	public void addFogOfWarCircle(@NotNull ScaledMap map, int radius,
								  int offsetX, int offsetY) {
		FogOfWarCircle circle = new FogOfWarCircle();
		circle.setMap(map);
		circle.setOffsetX(offsetX);
		circle.setOffsetY(offsetY);
		circle.setRadius(radius);
		shapeDAO.save(circle);

	}

	@Transactional
	public void addFogOfWarRect(@NotNull ScaledMap map, int width, int height,
								int offsetX, int offsetY) {
		FogOfWarRect rect = new FogOfWarRect();
		rect.setMap(map);
		rect.setHeight(height);
		rect.setWidth(width);
		rect.setOffsetX(offsetX);
		rect.setOffsetY(offsetY);
		shapeDAO.save(rect);
	}

	@Transactional
	public void addFogOfWarTriangle(@NotNull ScaledMap map, int width,
									int height, int offsetX, int offsetY,
									@NotNull TriangleOrientation orientation) {
		FogOfWarTriangle triangle = new FogOfWarTriangle();
		triangle.setMap(map);
		triangle.setVerticalSide(height);
		triangle.setHorizontalSide(width);
		triangle.setOffsetX(offsetX);
		triangle.setOffsetY(offsetY);
		triangle.setOrientation(orientation);
		shapeDAO.save(triangle);
	}

	@Transactional
	public TypedResult<FogOfWarGroup> createGroup(@NotNull ScaledMap map,
												  @NotNull String name, @NotNull List<FogOfWarShape> shapes) {
		if (shapes.isEmpty()) {
			return TypedResult.fail("No shapes selected");
		}

		FogOfWarGroup group = new FogOfWarGroup();
		group.setMap(map);
		group.setName(name);
		groupDAO.save(group);

		shapes.forEach(shape -> {
			shape.setGroup(group);
			shapeDAO.update(shape);
		});

		return TypedResult.ok(group);
	}

	@Transactional
	public TypedResult<FogOfWarGroup> editGroup(@NotNull FogOfWarGroup group,
												@NotNull String name, @NotNull List<FogOfWarShape> keep,
												@NotNull List<FogOfWarShape> remove) {
		if (keep.isEmpty()) {
			return TypedResult.fail("No shapes selected");
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

		return TypedResult.ok(group);
	}

	@Transactional
	public void setGroupVisibility(@NotNull MapView view,
								   @NotNull FogOfWarGroup group, @NotNull VisibilityStatus status) {
		internalSetGroupVisibility(view, group, status);

		refreshView(view);
	}

	private void internalSetGroupVisibility(@NotNull MapView view,
											@NotNull FogOfWarGroup group, @NotNull VisibilityStatus status) {
		FogOfWarGroupVisibilityFilter filter = new FogOfWarGroupVisibilityFilter();
		filter.view().set(view);
		filter.group().set(group);

		Option<FogOfWarGroupVisibility> optVisibility = groupVisibilityDAO
				.getUniqueByFilter(filter);

		if (optVisibility.isEmpty()) {
			FogOfWarGroupVisibility visibility = new FogOfWarGroupVisibility();
			visibility.setGroup(group);
			visibility.setView(view);
			visibility.setStatus(status);
			groupVisibilityDAO.save(visibility);
		} else {
			FogOfWarGroupVisibility visibility = optVisibility.get();
			visibility.setStatus(status);
			groupVisibilityDAO.update(visibility);
		}
		groupVisibilityDAO.flush();
	}

	@Transactional
	public void setShapeVisibility(@NotNull MapView view,
								   @NotNull FogOfWarShape shape, @NotNull VisibilityStatus status) {
		FogOfWarShapeVisibilityFilter filter = new FogOfWarShapeVisibilityFilter();
		filter.view().set(view);
		filter.shape().set(shape);

		Option<FogOfWarShapeVisibility> optVisibility = shapeVisibilityDAO
				.getUniqueByFilter(filter);

		if (optVisibility.isEmpty()) {
			FogOfWarShapeVisibility visibility = new FogOfWarShapeVisibility();
			visibility.setShape(shape);
			visibility.setView(view);
			visibility.setStatus(status);
			shapeVisibilityDAO.save(visibility);
		} else {
			FogOfWarShapeVisibility visibility = optVisibility.get();
			visibility.setStatus(status);
			shapeVisibilityDAO.update(visibility);
		}
		shapeVisibilityDAO.flush();

		refreshView(view);
	}

	@Transactional
	public TypedResult<TokenDefinition> createToken(@NotNull BeholderUser user,
													@Nullable Campaign campaign, @NotNull String name, int diameter,
													@NotNull byte[] image) {
		TypedResult<String> uploadResult = remoteImageService
				.uploadImage(ImageType.TOKEN, image);

		if (uploadResult.isOk()) {
			TokenDefinition def = new TokenDefinition();
			def.setOwner(user);
			def.setDiameterInSquares(diameter);
			def.setName(name);
			def.setAmazonKey(uploadResult.getObject());
			def.setCampaign(campaign);

			tokenDefinitionDAO.save(def);

			return TypedResult.ok(def);
		}

		return uploadResult.map(s -> null);
	}

	@Transactional
	public TypedResult<Portrait> createPortrait(@NotNull BeholderUser user,
												@Nullable Campaign campaign, @NotNull String name,
												@NotNull byte[] image) {
		TypedResult<String> uploadResult = remoteImageService
				.uploadImage(ImageType.PORTRAIT, image);

		if (uploadResult.isOk()) {

			Portrait portrait = new Portrait();
			portrait.setOwner(user);
			portrait.setCampaign(campaign);
			portrait.setName(name);
			portrait.setAmazonKey(uploadResult.getObject());

			portraitDAO.save(portrait);

			return TypedResult.ok(portrait);
		}

		return uploadResult.map(s -> null);
	}

	@Transactional
	public void selectPortrait(@NotNull MapView view,
							   @NotNull Portrait portrait,
							   @NotNull PortraitVisibilityLocation location) {
		Set<PortraitVisibilityLocation> excludedLocations = location
				.getExcludedLocations();
		removeVisibilities(view,
						   l -> l == location || excludedLocations.contains(l));

		PortraitVisibility visibility = new PortraitVisibility();
		visibility.setLocation(location);
		visibility.setPortrait(portrait);
		visibility.setView(view);

		portraitVisibilityDAO.save(visibility);

		view.getPortraitVisibilities().add(visibility);

		BeholderRegistry.instance
				.sendToView(view.getId(), s -> !s.isPreviewMode(),
							new UpdatePortraits(view));
	}

	private void removeVisibilities(@NotNull MapView view,
									Predicate<PortraitVisibilityLocation> locationPredicate) {
		Set<PortraitVisibility> toDelete = new HashSet<>();

		for (PortraitVisibility visibility : view.getPortraitVisibilities()) {
			if (locationPredicate.test(visibility.getLocation())) {
				toDelete.add(visibility);
			}
		}

		toDelete.forEach(view.getPortraitVisibilities()::remove);
		toDelete.forEach(portraitVisibilityDAO::delete);
	}

	@Transactional
	public void unselectPortrait(@NotNull MapView view,
								 @NotNull Portrait portrait,
								 @NotNull PortraitVisibilityLocation location) {
		removeVisibilities(view, location::equals);
		BeholderRegistry.instance
				.sendToView(view.getId(), s -> !s.isPreviewMode(),
							new UpdatePortraits(view));
	}

	@Transactional
	public void ungroup(@NotNull FogOfWarGroup group) {
		ScaledMap map = group.getMap();

		group.getShapes().forEach(s -> {
			s.setGroup(null);
			shapeDAO.update(s);
		});

		group.getVisibilities().forEach(groupVisibilityDAO::delete);

		groupDAO.delete(group);

		map.getSelectedBy().forEach(this::refreshView);

	}

	@Transactional
	public void deleteShape(@NotNull FogOfWarShape shape) {
		ScaledMap map = shape.getMap();

		shape.getVisibilities().forEach(shapeVisibilityDAO::delete);

		shapeDAO.delete(shape);

		map.getSelectedBy().forEach(this::refreshView);
	}

	@Transactional
	public void setTokenBorderType(@NotNull TokenInstance instance,
								   @NotNull TokenBorderType type) {
		instance.setBorderType(type);
		tokenInstanceDAO.update(instance);

		instance.getMap().getSelectedBy().forEach(this::refreshView);

	}

	@Transactional
	public void setTokenHP(@NotNull TokenInstance instance, Integer currentHP,
						   Integer maxHP) {
		tokenInstanceDAO.load(instance.getId()).map(inst -> {

			inst.setCurrentHitpoints(currentHP);
			inst.setMaxHitpoints(maxHP);
			tokenInstanceDAO.update(inst);

			inst.getMap().getSelectedBy().forEach(this::refreshView);

			return inst;
		});
	}

	@Transactional
	public void showToken(@NotNull TokenInstance instance) {
		instance.setShow(true);
		tokenInstanceDAO.update(instance);

		instance.getMap().getSelectedBy().forEach(this::refreshView);
	}

	@Transactional
	public void hideToken(@NotNull TokenInstance instance) {
		instance.setShow(false);
		tokenInstanceDAO.update(instance);

		instance.getMap().getSelectedBy().forEach(this::refreshView);
	}

	@Transactional
	public void setTokenNote(@NotNull TokenInstance instance, String note) {
		instance.setNote(note);
		tokenInstanceDAO.update(instance);
	}

	@Transactional
	public void updateTokenLocation(@NotNull TokenInstance instance, int x,
									int y) {
		instance.setOffsetX(x);
		instance.setOffsetY(y);
		tokenInstanceDAO.update(instance);

		instance.getMap().getSelectedBy().forEach(this::refreshView);
	}

	@Transactional
	public void updateParticipantPosition(InitiativeParticipant participant, int x, int y) {
		participant.setOffsetX(x);
		participant.setOffsetY(y);
		participantDAO.update(participant);

		refreshView(participant.getView());
	}

	@Transactional
	public void refreshView(@NotNull MapView view) {
		internalUpdateView(view, s -> true);
	}

	private void internalUpdateView(MapView view,
									Predicate<RegistryEntry> selector) {
		ScaledMap selectedMap = view.getSelectedMap();
		if (selectedMap == null) {
			BeholderRegistry.instance
					.sendToView(view.getId(), selector, new ClearMap());
		} else {
			updatePreview(view, selector, selectedMap);
			updateMainView(view, selector, selectedMap);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void initializeView(long viewId, @NotNull String sessionId,
							   boolean previewMode) {
		viewDAO.load(viewId).map(view -> {
			internalUpdateView(view, e -> e.getSessionId().equals(sessionId)
					&& Boolean.compare(e.isPreviewMode(), previewMode) == 0);
			BeholderRegistry.instance.sendToView(view.getId(),
												 e -> e.getSessionId().equals(sessionId) && !e.isPreviewMode(),
												 view.getInitiativeJS());
			BeholderRegistry.instance
					.sendToView(view.getId(), s -> !s.isPreviewMode(),
								new UpdatePortraits(view));

			return view;
		});
	}

	@Transactional
	public void gatherPlayerTokens(@NotNull MapView view, int x, int y) {
		InitiativeParticipantFilter filter = new InitiativeParticipantFilter();
		filter.player(true);
		filter.view(view);

		Seq<InitiativeParticipant> participants = participantDAO
				.findByFilter(filter);

		double angle = Math.toRadians(360.0 / (double) participants.size());

		int distance = Optional.ofNullable(view.getSelectedMap())
							   .map(ScaledMap::getSquareSize).map(s -> s * participants.size())
							   .orElse(view.getHeight() / 10);

		double nextAngle = 0.0;

		int i = 0;

		for (InitiativeParticipant participant : participants) {
			double ax = Math.cos(nextAngle);
			double ay = Math.sin(nextAngle);

			double dx = ax * distance;
			double dy = ay * distance;

			log.info("P{}: θ{} | X{} Y{}", i++, Math.toDegrees(nextAngle), dx,
					 dy);

			double nx = x + dx;
			double ny = y + dy;

			participant.setOffsetX((int) Math.round(nx));
			participant.setOffsetY((int) Math.round(ny));

			participantDAO.update(participant);

			nextAngle += angle;
		}

		internalUpdateView(view, s -> !s.isPreviewMode());
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
									Predicate<RegistryEntry> selector, MapView view, ScaledMap map) {
		double factor = previewMode ?
				map.getPreviewFactor() :
				map.getDisplayFactor(view);

		MapRenderable renderable = mapToJS(dimensions, previewMode, view, map,
										   factor);

		BeholderRegistry.instance
				.sendToView(view.getId(), selector, renderable);
	}

	@Transactional
	public void focusOnGroup(@NotNull MapView view, FogOfWarGroup group) {
		internalFocusOnGroup(view, group);
	}

	@Transactional
	public void selectMapAndSetFocus(@NotNull MapView view,
									 @NotNull FogOfWarGroup group) {
		view.setSelectedMap(group.getMap());
		viewDAO.update(view);
		viewDAO.flush();

		internalFocusOnGroup(view, group);
	}

	private void internalFocusOnGroup(@NotNull MapView view,
									  @NotNull FogOfWarGroup group) {
		internalSetGroupVisibility(view, group, VisibilityStatus.VISIBLE);

		double displayFactor = group.getMap().getDisplayFactor(view);

		Integer x = group.getShapes().stream()
						 .map(s -> s.visit(new FogOfWarShapeXCoordinateVisitor()))
						 .min(Comparator.naturalOrder()).map(i -> (int) (i * displayFactor))
						 .orElse(null);

		Integer y = group.getShapes().stream()
						 .map(s -> s.visit(new FogOfWarShapeYCoordinateVisitor()))
						 .min(Comparator.naturalOrder()).map(i -> (int) (i * displayFactor))
						 .orElse(null);

		if (x != null && y != null) {
			BeholderRegistry.instance.sendToView(view.getId(),
												 new CompoundRenderable(
														 mapToJS(group.getMap().getDisplayDimension(view), false,
																 view, group.getMap(), displayFactor),
														 new JSScrollCommand(x, y)));
		}
	}

	private MapRenderable mapToJS(Dimension dimensions, boolean previewMode,
								  MapView view, ScaledMap map, double factor) {
		MapRenderable renderable = new MapRenderable();
		renderable.setSrc(map.getImageUrl());
		renderable.setWidth((int) dimensions.getWidth());
		renderable.setHeight((int) dimensions.getHeight());

		List<JSToken> tokens = map.getTokens().stream().filter(TokenInstance::isShow)
								  .filter(t -> t.isVisible(view, previewMode))
								  .map(t -> tokenToJS(t, factor)).collect(Collectors.toList());
		if (view.isShowPlayers()) {
			tokens.addAll(view
								  .getInitiativeParticipants()
								  .stream()
								  .filter(InitiativeParticipant::hasPosition)
								  .filter(InitiativeParticipant::isPlayer)
								  .map(t -> initiativeParticipantToJS(t, map.getSquareSize(), factor))
								  .collect(Collectors.toList()));
		}
		renderable.setTokens(
				tokens);
		if (previewMode) {
			renderable.getTokens().forEach(t -> t.setLabel(null));
		}
		renderable.setAreaMarkers(
				view.getMarkers().stream().map(a -> markerToJS(a, factor))
					.collect(Collectors.toList()));
		renderable.setRevealed(map.getFogOfWarShapes().stream()
								  .filter(s -> shouldRender(s, view, previewMode))
								  .map(s -> s.visit(new FogOfWarShapeToJSShapeVisitor(factor)))
								  .collect(Collectors.toList()));
		return renderable;
	}

	private JSToken initiativeParticipantToJS(InitiativeParticipant participant, int squaresize, double factor) {
		JSToken token = new JSToken();
		token.setBorderType(TokenBorderType.Player.name());
		token.setBorderIntensity(TokenBorderIntensity.HEALTHY.name());
		token.setHeight(
				(int) (squaresize * factor));
		token.setWidth(
				(int) (squaresize * factor));
		token.setDiameterInSquares(
				squaresize);
		token.setLabel(participant.getName());

		token.setSrc("../img/player.png");
		token.setX((int) (participant.getOffsetX() * factor));
		token.setY((int) (participant.getOffsetY() * factor));

		return token;
	}

	private boolean shouldRender(FogOfWarShape shape, MapView view,
								 boolean previewMode) {
		FogOfWarGroup group = shape.getGroup();

		if (group != null) {
			return getStatus(group, view).isVisible(previewMode);
		}

		return getStatus(shape, view).isVisible(previewMode);
	}

	private VisibilityStatus getStatus(FogOfWarShape shape, MapView view) {
		return shapeVisibilityDAO
				.findByFilter(new FogOfWarShapeVisibilityFilter().shape(shape))
				.find(v -> v.getView().equals(view))
				.map(FogOfWarVisibility::getStatus)
				.getOrElse(VisibilityStatus.INVISIBLE);
	}

	private VisibilityStatus getStatus(FogOfWarGroup group, MapView view) {
		return groupVisibilityDAO.findByFilter(
				new FogOfWarGroupVisibilityFilter().group(group).view(view))
								 .map(FogOfWarVisibility::getStatus)
								 .getOrElse(VisibilityStatus.INVISIBLE);
	}

	private static JSAreaMarker markerToJS(AreaMarker marker, double factor) {
		ScaledMap map = marker.getView().getSelectedMap();
		int squareSizeInPixels = Optional.ofNullable(map)
										 .map(ScaledMap::getSquareSize).orElse(0);

		JSAreaMarker jsMarker = new JSAreaMarker();
		jsMarker.setColor("#".concat(marker.getColor()));
		jsMarker.setShape(marker
								  .visit(new AreaMarkerShapeVisitor(squareSizeInPixels, factor)));

		return jsMarker;
	}

	private static JSToken tokenToJS(TokenInstance instance, double factor) {
		JSToken token = new JSToken();
		token.setBorderType(instance.getBorderType().name());
		token.setBorderIntensity(instance.getBorderIntensity().name());
		token.setHeight(
				(int) (instance.getMap().getSquareSize() * factor * instance
						.getDefinition().getDiameterInSquares()));
		token.setWidth(
				(int) (instance.getMap().getSquareSize() * factor * instance
						.getDefinition().getDiameterInSquares()));
		token.setDiameterInSquares(
				instance.getDefinition().getDiameterInSquares());
		token.setLabel(instance.getLabel());

		if (instance.getStatusEffect() != null)
			token.setStatusEffect(instance.getStatusEffect().toString());

		// Workaround, will be transformed to URL
		token.setSrc(instance.getDefinition().getImageUrl());
		token.setX((int) (instance.getOffsetX() * factor));
		token.setY((int) (instance.getOffsetY() * factor));

		return token;
	}

	@NotNull
	@Transactional
	public MapLink createLink(@NotNull FogOfWarGroup source,
							  @NotNull FogOfWarGroup target) {
		MapLinkFilter filter = new MapLinkFilter();
		filter.sourceGroup(source);
		filter.targetGroup(target);

		if (mapLinkDAO.countByFilter(filter) == 0) {
			MapLink link = new MapLink();
			link.setSourceGroup(source);
			link.setTargetGroup(target);

			mapLinkDAO.save(link);

			return link;
		}

		return mapLinkDAO.getUniqueByFilter(filter).getOrNull();
	}

	@Transactional
	public TypedResult<Integer> doorbell(@NotNull String username, @Nullable String message) {

		MapViewFilter filter = new MapViewFilter();
		filter.listenToDoorbell(true);

		return TypedResult.ok(viewDAO.findByFilter(filter).map(MapView::getId).map(
				id -> {
					BeholderRegistry.instance
							.sendToView(id, new RingDoorbell(username, message));
					return id;
				}).count(v -> true));
	}

	@Transactional
	public void setTokenStatusEffect(@NotNull TokenInstance instance, @Nullable TokenStatusEffect statusEffect) {
		instance.setStatusEffect(statusEffect);
		tokenInstanceDAO.update(instance);

		instance.getMap().getSelectedBy().forEach(this::refreshView);
	}
}
